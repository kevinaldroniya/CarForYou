package com.car.foryou.helper;

import com.car.foryou.utils.EncryptionProperties;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Component
public class EncryptionHelper {

    private final EncryptionProperties encryptionProperties;


    public EncryptionHelper(EncryptionProperties encryptionProperties) {
        this.encryptionProperties = encryptionProperties;
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
}
