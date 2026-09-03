package za.ac.cput.marginhotelmanagement.service;
/* StaffServiceImpl.java
   Staff Service Implementation
   Author: Lithabile Lalela (221340963)
   Date: 12 July 2026 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.marginhotelmanagement.domain.Manager;
import za.ac.cput.marginhotelmanagement.domain.Receptionist;
import za.ac.cput.marginhotelmanagement.repository.ManagerRepository;
import za.ac.cput.marginhotelmanagement.repository.ReceptionistRepository;

import java.util.List;

@Service
public class StaffService implements IStaffService {

    private final ManagerRepository managerRepository;
    private final ReceptionistRepository receptionistRepository;

    @Autowired
    public StaffService(ManagerRepository managerRepository,
                        ReceptionistRepository receptionistRepository) {
        this.managerRepository = managerRepository;
        this.receptionistRepository = receptionistRepository;
    }

    @Override
    public Manager createManager(Manager manager) {
        return managerRepository.save(manager);
    }

    @Override
    public Manager readManager(Long id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Manager not found with id: " + id));
    }

    @Override
    public Manager updateManager(Manager manager) {
        if (!managerRepository.existsById(manager.getStaffId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Manager not found with id: " + manager.getStaffId());
        }
        return managerRepository.save(manager);
    }

    @Override
    public void deleteManager(Long id) {
        if (!managerRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Manager not found with id: " + id);
        }
        managerRepository.deleteById(id);
    }

    @Override
    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    @Override
    public Receptionist createReceptionist(Receptionist receptionist) {
        return receptionistRepository.save(receptionist);
    }

    @Override
    public Receptionist readReceptionist(Long id) {
        return receptionistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Receptionist not found with id: " + id));
    }

    @Override
    public Receptionist updateReceptionist(Receptionist receptionist) {
        if (!receptionistRepository.existsById(receptionist.getStaffId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Receptionist not found with id: " + receptionist.getStaffId());
        }
        return receptionistRepository.save(receptionist);
    }

    @Override
    public void deleteReceptionist(Long id) {
        if (!receptionistRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Receptionist not found with id: " + id);
        }
        receptionistRepository.deleteById(id);
    }

    @Override
    public List<Receptionist> getAllReceptionists() {
        return receptionistRepository.findAll();
    }
}