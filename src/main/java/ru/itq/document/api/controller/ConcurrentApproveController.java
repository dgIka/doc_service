package ru.itq.document.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itq.document.api.dto.request.ConcurrentApproveRequest;
import ru.itq.document.api.dto.response.ConcurrentApproveResponse;
import ru.itq.document.service.ConcurrentApproveService;

@RestController
@RequestMapping("/api/concurrent-approve")
@RequiredArgsConstructor
public class ConcurrentApproveController {

    private final ConcurrentApproveService concurrentApproveService;

    @PostMapping
    public ConcurrentApproveResponse approve(@Valid @RequestBody ConcurrentApproveRequest request) {
        return concurrentApproveService.run(
                request.getDocumentId(),
                request.getThreads(),
                request.getAttempts()
        );
    }
}
