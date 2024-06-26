package com.busanit501.teamproject2.hjt.repository;

import com.busanit501.teamproject2.hjt.domain.HjtTripBoard;
import com.busanit501.teamproject2.hjt.repository.search.HjtBoardSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HjtBoardRepository extends JpaRepository<HjtTripBoard, Long>, HjtBoardSearch {
    Page<HjtTripBoard> findByTripTitleContainingOrderByTripBnoDesc (String tripTitle, String keyword, Pageable pageable);

    @Query("select  b from HjtTripBoard b where b.tripTitle  like concat('%',:keyword ,'%')")
    Page<HjtTripBoard> findByKeyword (String keyword, Pageable pageable);
    @Query(value = "select now()", nativeQuery = true)
    String getTime();
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select h from HjtTripBoard h where h.tripBno=:tripBno")
    Optional<HjtTripBoard> findByIdWithImages(Long tripBno);

}