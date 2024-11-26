package com.example.zero2dev.responses;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SortResponse {
    private boolean empty;
    private boolean sorted;
    private boolean unsorted;
}
