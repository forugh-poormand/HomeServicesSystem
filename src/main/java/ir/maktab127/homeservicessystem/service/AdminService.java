package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.MainServiceDto;
import ir.maktab127.homeservicessystem.dto.SubServiceRequestDto;
import ir.maktab127.homeservicessystem.entity.MainService;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.SubService;
import java.util.List;

public interface AdminService {
    MainService createMainService(MainServiceDto dto);
    SubService createSubService(SubServiceRequestDto dto);
    Specialist confirmSpecialist(Long specialistId);
    void assignSpecialistToSubService(Long specialistId, Long subServiceId);
    List<Specialist> findAllUnconfirmedSpecialists();
}