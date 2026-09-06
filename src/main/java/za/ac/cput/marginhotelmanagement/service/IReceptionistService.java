package za.ac.cput.marginhotelmanagement.service;

import za.ac.cput.marginhotelmanagement.domain.Receptionist;

import java.util.List;

public interface IReceptionistService {
    Receptionist createReceptionist(Receptionist receptionist);
    Receptionist readReceptionist(Long id);
    Receptionist updateReceptionist(Receptionist receptionist);
    void deleteReceptionist(Long id);
    List<Receptionist> getAllReceptionists();
}
