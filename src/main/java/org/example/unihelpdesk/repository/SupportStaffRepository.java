package org.example.unihelpdesk.repository;

import org.example.unihelpdesk.model.SupportStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportStaffRepository extends JpaRepository<SupportStaff, Integer> {
}