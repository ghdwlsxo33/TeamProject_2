package com.busanit501.teamproject2.hjt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class HjtPageResponseDTO<E> {
    private int page;
    private int size;
    private int total;
    private int start;
    private int end;
    private boolean prev;
    private boolean next;
    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public HjtPageResponseDTO(HjtPageRequestDTO hjtPageRequestDTO,
                              List<E> dtoList, int total) {
        if (total <= 0) {
            return;
        }

        this.page = hjtPageRequestDTO.getPage();
        this.size = hjtPageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList;

        this.end = (int) (Math.ceil(this.page / 10.0)) * 10;
        this.start = this.end - 9;

        int last = (int) (Math.ceil(total / (double) size));

        this.end = end > last ? last : end;

        this.prev = this.start > 1;

        this.next = total > this.end * this.size;

    }
}