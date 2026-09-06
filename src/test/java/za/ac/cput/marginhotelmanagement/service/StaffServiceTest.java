package za.ac.cput.marginhotelmanagement.service;
/* StaffServiceImplTest.java
   Staff Service Test
   Author: Lithabile Lalela (221340963)
   Date: 12 July 2026 */

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.marginhotelmanagement.domain.*;
import za.ac.cput.marginhotelmanagement.factory.StaffFactory;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class StaffServiceTest {

    @Autowired
    private StaffService staffService;

    private Manager manager;
    private Receptionist receptionist;

    @BeforeEach
    void setUp() {
        Name name = new Name.Builder()
                .setFirstName("Lithabile")
                .setMiddleName("L")
                .setLastName("Lalela")
                .build();
        ContactDetails contactDetails = new ContactDetails.Builder()
                .setEmail("lithabile@marginhotel.com")
                .setMobile("0821234567")
                .build();
        manager = StaffFactory.createManager(1L, name, contactDetails, "Office 1A");
        receptionist = StaffFactory.createReceptionist(2L, name, contactDetails, "Desk 3B");
    }


    @Test
    void createManager() {
        Manager saved = staffService.createManager(manager);
        assertNotNull(saved);
        assertEquals("Office 1A", saved.getOfficeNumber());
        System.out.println("Manager created: " + saved);
    }

    @Test
    void readManager() {
        Manager saved = staffService.createManager(manager);
        Manager found = staffService.readManager(saved.getStaffId());
        assertNotNull(found);
        System.out.println("Manager found: " + found);
    }

    @Test
    void updateManager() {
        Manager saved = staffService.createManager(manager);
        Name newName = new Name.Builder()
                .setFirstName("Updated")
                .setMiddleName("L")
                .setLastName("Lalela")
                .build();
        ContactDetails cd = new ContactDetails.Builder()
                .setEmail("updated@marginhotel.com")
                .setMobile("0821234567")
                .build();
        Manager updated = new Manager.Builder()
                .copy(saved)
                .setName(newName)
                .setContactDetails(cd)
                .setOfficeNumber("Office 2B")
                .build();
        Manager result = staffService.updateManager(updated);
        assertNotNull(result);
        assertEquals("Office 2B", result.getOfficeNumber());
        System.out.println("Manager updated: " + result);
    }

    @Test
    @Disabled
    void deleteManager() {
        Manager saved = staffService.createManager(manager);
        staffService.deleteManager(saved.getStaffId());
        System.out.println("Manager deleted successfully");
    }

    @Test
    void getAllManagers() {
        staffService.createManager(manager);
        List<Manager> managers = staffService.getAllManagers();
        assertNotNull(managers);
        assertFalse(managers.isEmpty());
        System.out.println("All managers: " + managers);
    }

    @Test
    void createReceptionist() {
        Receptionist saved = staffService.createReceptionist(receptionist);
        assertNotNull(saved);
        assertEquals("Desk 3B", saved.getDeskNumber());
        System.out.println("Receptionist created: " + saved);
    }

    @Test
    void readReceptionist() {
        Receptionist saved = staffService.createReceptionist(receptionist);
        Receptionist found = staffService.readReceptionist(saved.getStaffId());
        assertNotNull(found);
        System.out.println("Receptionist found: " + found);
    }

    @Test
    void updateReceptionist() {
        Receptionist saved = staffService.createReceptionist(receptionist);
        Name newName = new Name.Builder()
                .setFirstName("Updated")
                .setMiddleName("L")
                .setLastName("Lalela")
                .build();
        ContactDetails cd = new ContactDetails.Builder()
                .setEmail("updated@marginhotel.com")
                .setMobile("0821234567")
                .build();
        Receptionist updated = new Receptionist.Builder()
                .copy(saved)
                .setName(newName)
                .setContactDetails(cd)
                .setDeskNumber("Desk 5A")
                .build();
        Receptionist result = staffService.updateReceptionist(updated);
        assertNotNull(result);
        assertEquals("Desk 5A", result.getDeskNumber());
        System.out.println("Receptionist updated: " + result);
    }

    @Test
    @Disabled
    void deleteReceptionist() {
        Receptionist saved = staffService.createReceptionist(receptionist);
        staffService.deleteReceptionist(saved.getStaffId());
        System.out.println("Receptionist deleted successfully");
    }

    @Test
    void getAllReceptionists() {
        staffService.createReceptionist(receptionist);
        List<Receptionist> receptionists = staffService.getAllReceptionists();
        assertNotNull(receptionists);
        assertFalse(receptionists.isEmpty());
        System.out.println("All receptionists: " + receptionists);
    }
}
