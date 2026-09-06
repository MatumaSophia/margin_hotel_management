package za.ac.cput.marginhotelmanagement.service;

import za.ac.cput.marginhotelmanagement.domain.Manager;

import java.util.List;

public interface IManagerService {
    Manager createManager(Manager manager);
    Manager readManager(Long id);
    Manager updateManager(Manager manager);
    void deleteManager(Long id);
    List<Manager> getAllManagers();
}
