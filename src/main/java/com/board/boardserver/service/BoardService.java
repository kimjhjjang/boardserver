package com.board.boardserver.service;

import com.board.boardserver.domain.Board;
import com.board.boardserver.dto.BoardRequest;
import com.board.boardserver.dto.BoardResponse;
import com.board.boardserver.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public BoardResponse createBoard(BoardRequest request) {
        if (boardRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Board name already exists");
        }

        Board board = Board.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Board savedBoard = boardRepository.save(board);
        return convertToResponse(savedBoard);
    }

    public BoardResponse getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        return convertToResponse(board);
    }

    public List<BoardResponse> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BoardResponse updateBoard(Long id, BoardRequest request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        if (!board.getName().equals(request.getName()) 
                && boardRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Board name already exists");
        }

        board.setName(request.getName());
        board.setDescription(request.getDescription());

        Board updatedBoard = boardRepository.save(board);
        return convertToResponse(updatedBoard);
    }

    @Transactional
    public void deleteBoard(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new IllegalArgumentException("Board not found");
        }
        boardRepository.deleteById(id);
    }

    private BoardResponse convertToResponse(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .createdAt(board.getCreatedAt())
                .build();
    }
}
