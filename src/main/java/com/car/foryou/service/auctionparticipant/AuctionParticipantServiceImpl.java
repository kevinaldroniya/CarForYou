package com.car.foryou.service.auctionparticipant;

import com.car.foryou.dto.auctionparticipant.AuctionParticipantRequest;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.auctionparticipant.AuctionRegistrationStatus;
import com.car.foryou.dto.auctionparticipant.CancelRegistrationRequest;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.dto.payment.PaymentMethod;
import com.car.foryou.exception.GeneralException;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceAlreadyExistsException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.mapper.AuctionParticipantMapper;
import com.car.foryou.model.AuctionParticipant;
import com.car.foryou.model.User;
import com.car.foryou.repository.AuctionParticipantRepository;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.service.item.ItemService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuctionParticipantServiceImpl implements AuctionParticipantService {
    private final AuctionParticipantRepository auctionParticipantRepository;
    private final ItemService itemService;

    public AuctionParticipantServiceImpl(AuctionParticipantRepository auctionParticipantRepository, ItemService itemService) {
        this.auctionParticipantRepository = auctionParticipantRepository;
        this.itemService = itemService;
    }

    @Override
    public List<AuctionParticipantResponse> getAllAuctionParticipants() {
        List<AuctionParticipant> auctionParticipants = auctionParticipantRepository.findAll();
        return auctionParticipants.stream().map(AuctionParticipantMapper::mapToAuctionParticipantResponse).toList();
    }

    @Transactional
    @Override
    public String register(Integer itemId, AuctionParticipantRequest request) {
        try {
            ItemResponse item = itemService.getItemById(itemId);
            User user = User.builder()
                    .id(CustomUserDetailService.getLoggedInUserDetails().getId())
                    .build();
            validateAuctionRegistration(request, item);
            AuctionParticipant auctionParticipant = AuctionParticipant.builder()
                    .itemId(item.getItemId())
                    .user(user)
                    .depositAmount(request.getDepositAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .registrationStatus(AuctionRegistrationStatus.REGISTERED)
                    .build();
            auctionParticipantRepository.save(auctionParticipant);
            return "You have successfully registered for the auction";
        }catch (InvalidRequestException e){
            throw new GeneralException(e.getMessage(), e.getStatus());
        }
    }

    @Override
    public String cancelRegistration(Integer itemId, CancelRegistrationRequest request) {
        ItemResponse item = itemService.getItemById(itemId);
        Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();
        AuctionParticipant participant = auctionParticipantRepository.findByItemIdAndUserId(itemId, userId).orElseThrow(
                () -> new ResourceNotFoundException("AuctionRegistration", "itemId", item.getItemId())
        );
        if (Instant.now().isAfter(item.getAuctionStart().toInstant().minusSeconds(60L * 60L * 2L))){
            throw new InvalidRequestException("You can't cancel your registration, the auction is about to start", HttpStatus.BAD_REQUEST);
        }
        participant.setRegistrationStatus(AuctionRegistrationStatus.CANCELLED);
        participant.setCancelReason(request.reason());
        auctionParticipantRepository.save(participant);
        return "You have successfully cancelled your registration, your deposit will be returned in 3 to 5 working days";
    }

    @Override
    public AuctionParticipantResponse refundDeposit(String registrationId) {
        AuctionParticipant participant = auctionParticipantRepository.findById(registrationId).orElseThrow(
                () -> new ResourceNotFoundException("AuctionRegistration", "ID", registrationId)
        );
        participant.setRegistrationStatus(AuctionRegistrationStatus.REFUNDED);
        AuctionParticipant saved = auctionParticipantRepository.save(participant);
        return AuctionParticipantMapper.mapToAuctionParticipantResponse(saved);
    }

    private void validateAuctionRegistration(AuctionParticipantRequest request, ItemResponse item) {
        if (request.getDepositAmount() == null || request.getPaymentMethod() == null){
            throw new InvalidRequestException("Deposit amount and payment method are required", HttpStatus.BAD_REQUEST);
        }

        if (!item.getStatus().equals(ItemStatus.AUCTION_SCHEDULED)){
            throw new InvalidRequestException("You can't register for the auction, the auction is not scheduled", HttpStatus.BAD_REQUEST);
        }
        if (Instant.now().isAfter(item.getAuctionStart().toInstant().minusSeconds(60L * 60L))){
            throw new InvalidRequestException("You can't register for the auction, the registration time has passed", HttpStatus.BAD_REQUEST);
        }

        Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();
        auctionParticipantRepository.findByItemIdAndUserId(item.getItemId(), userId).ifPresent(
                auctionParticipant -> {throw new ResourceAlreadyExistsException("AuctionParticipant", HttpStatus.CONFLICT);
                }
        );

        if (request.getPaymentMethod().equals(PaymentMethod.BANK_TRANSFER)){
            if (request.getDepositAmount() != 10000000){
                throw new InvalidRequestException("Deposit amount incorrect, your deposit will be returned", HttpStatus.BAD_REQUEST);
            }
        }else {
            request.setDepositAmount(10000000);
        }
    }
}
