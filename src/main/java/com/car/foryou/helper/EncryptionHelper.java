package com.car.foryou.helper;

import com.car.foryou.utils.EncryptionProperties;
import com.car.foryou.utils.IPaymuProperties;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Component
public class EncryptionHelper {

    private final EncryptionProperties encryptionProperties;
    private final IPaymuProperties iPaymuProperties;

    private static final String HMACSHA256 = "HmacSHA256";


    public EncryptionHelper(EncryptionProperties encryptionProperties, IPaymuProperties iPaymuProperties) {
        this.encryptionProperties = encryptionProperties;
        this.iPaymuProperties = iPaymuProperties;
    }

    public String encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] decodeSecretKey = Base64.getDecoder().decode(encryptionProperties.getSecretKey());
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodeSecretKey, 0, decodeSecretKey.length, encryptionProperties.getAlgorithm());
        Cipher cipher = Cipher.getInstance(encryptionProperties.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] compressed = compress(data);
        byte[] cipherText = cipher.doFinal(compressed);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(cipherText);
    }

    public String decrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] decode = Base64.getUrlDecoder().decode(data);
        byte[] decodeSecretKey = Base64.getDecoder().decode(encryptionProperties.getSecretKey());
        SecretKeySpec secretKeySpec =  new SecretKeySpec(decodeSecretKey, 0, decodeSecretKey.length, encryptionProperties.getAlgorithm());
        Cipher cipher = Cipher.getInstance(encryptionProperties.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] cipherText = cipher.doFinal(decode);
        return decompress(cipherText);
    }

    private byte[] compress(String data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(GZIPOutputStream gzip = new GZIPOutputStream(baos)){
            gzip.write(data.getBytes());
        }
        return baos.toByteArray();
    }

    private String decompress(byte[] data){
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try(GZIPInputStream gis =new GZIPInputStream(bais)){
            ByteArrayOutputStream baos =  new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = gis.read(buffer)) != -1){
                baos.write(buffer, 0, length);
            }
            return baos.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateDigest (String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        return Base64.getEncoder().encodeToString(digest);
    }

    public String generateSignature (String data) throws NoSuchAlgorithmException, InvalidKeyException {
        String digest = generateDigest(data);
        byte[] decodeSecret = encryptionProperties.getSecretKey().getBytes();
        SecretKey secretKey =  new SecretKeySpec(decodeSecret, 0, decodeSecret.length, HMACSHA256);
        Mac hmacSha256 = Mac.getInstance(HMACSHA256);
        hmacSha256.init(secretKey);
        hmacSha256.update(digest.getBytes());
        byte[] HmacSha256DigestBytes = hmacSha256.doFinal();
        return Base64.getEncoder().encodeToString(HmacSha256DigestBytes);
    }

    public String getSignatureIPaymu(String jsonBody){
        String requestBody = getSHA256Hash(jsonBody);
        String stringToSign = "POST:" + iPaymuProperties.getVirtualAccount() + ":" + requestBody + ":" + iPaymuProperties.getApiKey();
        return sha256_HMAC(iPaymuProperties.getApiKey(), stringToSign);
    }

    private String getSHA256Hash(String data){
       try {
           MessageDigest md = MessageDigest.getInstance("SHA-256");
           byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
           return bytesToHex(hash);
       } catch (NoSuchAlgorithmException e) {
           throw new RuntimeException(e);
       }
    }

    private String bytesToHex(byte[] hash){
        return DatatypeConverter.printHexBinary(hash);
    }

    private String sha256_HMAC(String secret, String message){
        String hash = "";
        try {
            Mac sha256HMAC = Mac.getInstance(HMACSHA256);
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), HMACSHA256);
            sha256HMAC.init(secretKey);
            byte[] bytes = sha256HMAC.doFinal(message.getBytes());
            hash = byteArrayToHexString(bytes);
        }catch (NoSuchAlgorithmException | InvalidKeyException e){
            e.printStackTrace();
        }
        return hash;
    }

    private String byteArrayToHexString(byte[] bytes){
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; bytes != null && n < bytes.length; n++) {
            stmp = Integer.toHexString(bytes[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }
}
