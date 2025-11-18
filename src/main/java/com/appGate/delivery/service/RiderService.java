package com.appGate.delivery.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.appGate.delivery.dto.RiderDto;
import com.appGate.delivery.dto.SuspendRiderDto;
import com.appGate.delivery.dto.UnblockRiderDto;
import com.appGate.delivery.models.Rider;
import com.appGate.delivery.repository.RiderRepository;
import com.appGate.delivery.response.BaseResponse;
import com.appGate.delivery.utils.FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@Service
public class RiderService {

    private final RiderRepository riderInfoRepository;

    public RiderService(RiderRepository riderInfoRepository) {
        this.riderInfoRepository = riderInfoRepository;
    }

    public BaseResponse createRider(RiderDto riderInfoDto, HttpServletRequest request){
       String baseUrl  = getBaseurl(request);

       return  new BaseResponse(HttpStatus.CREATED.value(),"Success", saveRider(riderInfoDto, baseUrl));
    }

    private   String getBaseurl(HttpServletRequest request){
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String forwardedPrefix = request.getHeader("X-Forwarded-Prefix");

        //Build the original URL
        StringBuilder originalUrl = new StringBuilder();

        // Protocol (http or https)
        if (forwardedProto != null){
            originalUrl.append(forwardedProto).append("://");
        } else{
            originalUrl.append(request.getScheme()).append("://");
        }

        // host and port
        if(forwardedHost != null){
            originalUrl.append(forwardedHost);
        } else {
            originalUrl.append(request.getServerName());
            if (request.getServerPort() != 80 && request.getServerPort() != 443) {
                originalUrl.append(":").append(request.getServerPort());
            }
        }

        // Path prefix if any
        if (forwardedPrefix != null){
            originalUrl.append(forwardedPrefix);
        }

        return originalUrl.toString();
    }

    private String saveImage(MultipartFile file,  String heroGallery, String baseUrl){
        String fileSavePath = "";
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try{
            fileSavePath = FileUploadUtil.uploadImage(heroGallery, FileUploadUtil.generateUniqName(fileName), file);
        } catch (IOException e){
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "error occurred while uploading image", e);
        }

        fileSavePath = baseUrl + "/api/users/rider/image/" + fileSavePath;

        System.out.println("file save path: " + fileSavePath);
        System.out.println("baseUrl " + baseUrl);

        return fileSavePath;
    }

    // save the rider info::
    private Rider saveRider(RiderDto riderInfoDto, String baseUrl){
        // create the rider instance::
        Rider riderInfo = new Rider();
        riderInfo.setSurName(riderInfoDto.getSurName());
        riderInfo.setOtherName(riderInfoDto.getOtherName());
        riderInfo.setContactAddress(riderInfoDto.getContactAddress());
        riderInfo.setOfficeAddress(riderInfoDto.getOfficeAddress());
        riderInfo.setDob(riderInfoDto.getDob());
        riderInfo.setEmail(riderInfoDto.getEmail());
        riderInfo.setPhoneNumber(riderInfoDto.getPhoneNumber());
        riderInfo.setNationality(riderInfoDto.getNationality());
        riderInfo.setNin(riderInfoDto.getNin());
        riderInfo.setBvn(riderInfoDto.getBvn());
        riderInfo.setNextOfKin(riderInfoDto.getNextOfKin());
        riderInfo.setNextOfKinAddress(riderInfoDto.getNextOfKinAddress());
        riderInfo.setPassport(saveImage(riderInfoDto.getPassport() , "riderGallery", baseUrl));
        riderInfo.setLicences(saveImage(riderInfoDto.getLicences() , "riderGallery", baseUrl));
        riderInfo.setSignature(saveImage(riderInfoDto.getSignature() , "riderGallery", baseUrl));
        riderInfo.setGender(riderInfoDto.getGender());

    return riderInfoRepository.save(riderInfo);
    }

    private void updateRiderInfo(Rider riderInfo, RiderDto riderInfoDto){
        riderInfo.setSurName(riderInfoDto.getSurName());
        riderInfo.setOtherName(riderInfoDto.getOtherName());
        riderInfo.setContactAddress(riderInfoDto.getContactAddress());
        riderInfo.setOfficeAddress(riderInfoDto.getOfficeAddress());
        riderInfo.setDob(riderInfoDto.getDob());
        riderInfo.setEmail(riderInfoDto.getEmail());
        riderInfo.setPhoneNumber(riderInfoDto.getPhoneNumber());
        riderInfo.setNationality(riderInfoDto.getNationality());
        riderInfo.setNin(riderInfoDto.getNin());
        riderInfo.setBvn(riderInfoDto.getBvn());
        riderInfo.setNextOfKin(riderInfoDto.getNextOfKin());
        riderInfo.setNextOfKinAddress(riderInfoDto.getNextOfKinAddress());
        riderInfo.setGender(riderInfoDto.getGender());
    }

    public BaseResponse getRiderDetails(Long riderId){
        return new BaseResponse(HttpStatus.OK.value(), "Success", getRider(riderId));
    }

    public Resource loadFileAsResource(String fileName){
        try {
            // Split by forward slash:.:
            String[] splitedFileName =  fileName.split("/");

            // The last element will be the actual filename
            String imageFile = splitedFileName[splitedFileName.length - 1];
            // Everything before the last slash is the directory
            String heroGallery = String.join("/", Arrays.copyOfRange(splitedFileName, 0, splitedFileName.length - 1));

            Path path = Paths.get(heroGallery);
            Path file = path.resolve(imageFile);
            Resource resource = new UrlResource(file.toUri());

            System.out.println("file url " + file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read the file!");

            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error:" + e.getMessage());
        }
    }

    public  BaseResponse updateRider(Long riderId, RiderDto riderInfoDto, HttpServletRequest request){
        Rider riderInfo = getRider(riderId);
        String baseUrl = getBaseurl(request);

        if (riderInfoDto.getPassport() != null && !riderInfoDto.getPassport().isEmpty()) {
            updateImage(riderInfo, riderInfoDto,  "passport", baseUrl);
        }

        if (riderInfoDto.getSignature() != null && !riderInfoDto.getSignature().isEmpty()) {
            updateImage(riderInfo, riderInfoDto,  "signature", baseUrl);
        }

        if (riderInfoDto.getLicences() != null && !riderInfoDto.getLicences().isEmpty()) {
            updateImage(riderInfo, riderInfoDto,  "licences", baseUrl);
        }

        updateRiderInfo(riderInfo, riderInfoDto);

        return new BaseResponse(HttpStatus.OK.value(), "successful", riderInfoRepository.save(riderInfo));
    }

    private void updateImage(Rider riderInfo, RiderDto riderInfoDto, String imageType, String baseUrl){
        if ("passport".equals(imageType) && riderInfoDto.getPassport() !=null) {
            try{
                FileUploadUtil.deleteFile(riderInfo.getPassport());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "error",e);
            }
            riderInfo.setPassport(saveImage(riderInfoDto.getPassport() , "passport", baseUrl));

        } else if ("signature".equals(imageType) && riderInfoDto.getSignature() !=null) {
            try{
                FileUploadUtil.deleteFile(riderInfo.getSignature());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "error",e);
            }
            riderInfo.setSignature(saveImage(riderInfoDto.getSignature() , "signature", baseUrl));

        } else if ("licences".equals(imageType) && riderInfoDto.getLicences() !=null) {
            try{
                FileUploadUtil.deleteFile(riderInfo.getLicences());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "error",e);
            }
            riderInfo.setLicences(saveImage(riderInfoDto.getLicences() , "licences", baseUrl));
        }
    }



    public  BaseResponse getAllRidersInfo(){
        return new BaseResponse(HttpStatus.OK.value(), "successful", riderInfoRepository.findAll());
    }


    public  BaseResponse suspendRider(Long riderId, SuspendRiderDto suspendRiderDto){
        Rider rider = getRider(riderId);

        rider.setSuspended(true);
        rider.setReasonForSuspension(suspendRiderDto.getReasonForSuspension());

        riderInfoRepository.save(rider);

        return new BaseResponse(HttpStatus.OK.value(),"successful",riderInfoRepository.findById(riderId).get());

    }

    private Rider getRider(Long riderId){
        return riderInfoRepository.findById(riderId).orElseThrow( ()  -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rider not found!"));
    }

    public  BaseResponse unBlockRider(Long riderId, UnblockRiderDto unblockRiderDto){
        Rider rider = getRider(riderId);

        rider.setSuspended(false);
        rider.setReasonForUnblocking(unblockRiderDto.getReasonForUnblocking());

        riderInfoRepository.save(rider);

        return new BaseResponse(HttpStatus.OK.value(), "successful", riderInfoRepository.findById(riderId).get());

    }

    public BaseResponse getAllSuspendedRider(boolean suspended){
        return  new BaseResponse(HttpStatus.OK.value(), "successful",riderInfoRepository.findBySuspended(suspended));
    }












}
