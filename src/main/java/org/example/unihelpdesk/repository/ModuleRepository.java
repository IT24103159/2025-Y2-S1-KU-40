package org.example.unihelpdesk.repository;

import org.example.unihelpdesk.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {}