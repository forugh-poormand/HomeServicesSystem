package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.dto.MainServiceDto;
import ir.maktab127.homeservicessystem.dto.SubServiceRequestDto;
import ir.maktab127.homeservicessystem.entity.MainService;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.SubService;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import ir.maktab127.homeservicessystem.exceptions.DuplicateResourceException;
import ir.maktab127.homeservicessystem.exceptions.ResourceNotFoundException;
import ir.maktab127.homeservicessystem.repository.MainServiceRepository;
import ir.maktab127.homeservicessystem.repository.SpecialistRepository;
import ir.maktab127.homeservicessystem.repository.SubServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final MainServiceRepository mainServiceRepository;
    private final SubServiceRepository subServiceRepository;
    private final SpecialistRepository specialistRepository;

    @Override
    public MainService createMainService(MainServiceDto dto) {
        mainServiceRepository.findByName(dto.name()).ifPresent(s -> {
            throw new DuplicateResourceException("Main service with this name already exists.");
        });
        MainService mainService = new MainService();
        mainService.setName(dto.name());
        return mainServiceRepository.save(mainService);
    }

    @Override
    public SubService createSubService(SubServiceRequestDto dto) {
        MainService mainService = mainServiceRepository.findById(dto.mainServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Main service not found with id: " + dto.mainServiceId()));

        boolean isDuplicate = mainService.getSubServices().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(dto.name()));
        if (isDuplicate) {
            throw new DuplicateResourceException("Sub-service with this name already exists in this main service.");
        }

        SubService subService = new SubService();
        subService.setName(dto.name());
        subService.setBasePrice(dto.basePrice());
        subService.setDescription(dto.description());
        subService.setMainService(mainService);
        return subServiceRepository.save(subService);
    }

    @Override
    public Specialist confirmSpecialist(Long specialistId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id: " + specialistId));
        specialist.setStatus(SpecialistStatus.CONFIRMED);
        return specialistRepository.save(specialist);
    }

    @Override
    public void assignSpecialistToSubService(Long specialistId, Long subServiceId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id: " + specialistId));
        SubService subService = subServiceRepository.findById(subServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-service not found with id: " + subServiceId));

        specialist.getExpertIn().add(subService);
        specialistRepository.save(specialist);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialist> findAllUnconfirmedSpecialists() {
        return specialistRepository.findAll().stream()
                .filter(s -> s.getStatus() == SpecialistStatus.AWAITING_CONFIRMATION)
                .collect(Collectors.toList());
    }
}