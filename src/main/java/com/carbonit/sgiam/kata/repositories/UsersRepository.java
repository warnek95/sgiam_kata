package com.carbonit.sgiam.kata.repositories;

import com.carbonit.sgiam.kata.models.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface UsersRepository extends PagingAndSortingRepository<User, UUID> {
}
