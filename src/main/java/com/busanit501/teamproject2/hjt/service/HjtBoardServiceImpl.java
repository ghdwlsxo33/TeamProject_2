package com.busanit501.teamproject2.hjt.service;

import com.busanit501.teamproject2.hjt.domain.HjtTripBoard;
import com.busanit501.teamproject2.hjt.domain.HjtTripReply;
import com.busanit501.teamproject2.hjt.dto.*;
import com.busanit501.teamproject2.hjt.repository.HjtBoardRepository;
import com.busanit501.teamproject2.hjt.repository.HjtReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class HjtBoardServiceImpl implements HjtBoardService {
    private final HjtBoardRepository hjtBoardRepository;
    private final HjtReplyRepository hjtReplyRepository;
    private final ModelMapper modelMapper;


    @Override
    public Long register(HjtBoardDTO hjtBoardDTO) {
        HjtTripBoard hjtTripBoard = dtoToEntity(hjtBoardDTO);
        Long tripBno = hjtBoardRepository.save(hjtTripBoard).getTripBno();
        return tripBno;
    }

    @Override
    public HjtBoardDTO read(Long tripBno) {
        Optional<HjtTripBoard> result = hjtBoardRepository.findByIdWithImages(tripBno);
        HjtTripBoard hjtTripBoard = result.orElseThrow();
        HjtBoardDTO hjtBoardDTO = entityToDTO(hjtTripBoard);
        return hjtBoardDTO;
    }

    @Override
    public void update(HjtBoardDTO hjtBoardDTO) {
        Optional<HjtTripBoard> result = hjtBoardRepository.findById(hjtBoardDTO.getTripBno());
        HjtTripBoard hjtTripBoard = result.orElseThrow();
        hjtTripBoard.changeTripTitleAndContent(hjtBoardDTO.getTripTitle(),hjtBoardDTO.getTripContent());
        hjtTripBoard.clearImages();

        if(hjtBoardDTO.getFileNames() != null) {
            for(String fileName : hjtBoardDTO.getFileNames()){
                String[] arr = fileName.split("_");
                hjtTripBoard.addImage(arr[0],arr[1]);
            }
        }
        hjtBoardRepository.save(hjtTripBoard);
    }

    @Override
    public void delete(Long tripBno) {
        hjtBoardRepository.deleteById(tripBno);
    }

    @Override
    public void deleteAll(Long tripBno) {
        List<HjtTripReply> result = hjtReplyRepository.findByHjtTripBoard_TripBno(tripBno);
        boolean checkReply = !result.isEmpty() ? false : true;
        if(checkReply){
            hjtReplyRepository.deleteByHjtTripBoard_TripBno(tripBno);
        }

        //게시글 삭제와 첨부 이미지 삭제(썸네일 이미지 삭제도 포함) 포함
        hjtReplyRepository.deleteById(tripBno);
    }


    @Override
    public HjtPageResponseDTO<HjtBoardDTO> list(HjtPageRequestDTO hjtPageRequestDTO) {
        String[] types = hjtPageRequestDTO.getTypes();
        String keyword = hjtPageRequestDTO.getKeyword();
        Pageable pageable = hjtPageRequestDTO.getPageable("tripBno");
        // 검색어, 페이징 처리가 된 결과물 10개.
        Page<HjtTripBoard> result = hjtBoardRepository.searchAll(types,keyword,pageable);
        List<HjtBoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board,HjtBoardDTO.class))
                .collect(Collectors.toList());

        // 서버 -> 화면에 전달할 준비물 준비 작업 완료.
        // 1)페이지 2) 사이즈 3) 전쳇갯수 4) 검색 결과 내역10개(엔티티-> DTO)
        HjtPageResponseDTO hjtPageResponseDTO = HjtPageResponseDTO.<HjtBoardDTO>withAll()
                .hjtPageRequestDTO(hjtPageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();

        return hjtPageResponseDTO;
    }

    @Override
    public HjtPageResponseDTO<HjtBoardListReplyCountDTO> listWithReplyCount(HjtPageRequestDTO hjtPageRequestDTO) {
        String[] types = hjtPageRequestDTO.getTypes();
        String keyword = hjtPageRequestDTO.getKeyword();
        Pageable pageable = hjtPageRequestDTO.getPageable("tripBno");

        Page<HjtBoardListReplyCountDTO> result = hjtBoardRepository.searchWithReplyCount(types,keyword,pageable);

        HjtPageResponseDTO hjtPageResponseDTO = HjtPageResponseDTO.<HjtBoardListReplyCountDTO>withAll()
                .hjtPageRequestDTO(hjtPageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
        return hjtPageResponseDTO;
    }

    @Override
    public HjtPageResponseDTO<BoardListAllDTO> listWithAll(HjtPageRequestDTO hjtPageRequestDTO) {
        String[] types = hjtPageRequestDTO.getTypes();
        String keyword = hjtPageRequestDTO.getKeyword();
        Pageable pageable = hjtPageRequestDTO.getPageable("tripBno");

        Page<BoardListAllDTO> result = hjtBoardRepository.searchWithAll(types,keyword,pageable);

        HjtPageResponseDTO<BoardListAllDTO> hjtPageResponseDTO =
                HjtPageResponseDTO.<BoardListAllDTO>withAll()
                        .hjtPageRequestDTO(hjtPageRequestDTO)
                        .dtoList(result.getContent())
                        .total((int) result.getTotalElements())
                        .build();

        return hjtPageResponseDTO;
    }
}