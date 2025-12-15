package com.appGate.goodsrecovery.controller;

import com.appGate.goodsrecovery.dto.RecoveryAgentLoginDto;
import com.appGate.goodsrecovery.response.BaseResponse;
import com.appGate.goodsrecovery.service.RecoveryAgentAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goods-recovery/auth")
public class RecoveryAgentAuthController {

    private final RecoveryAgentAuthService recoveryAgentAuthService;

    public RecoveryAgentAuthController(RecoveryAgentAuthService recoveryAgentAuthService) {
        this.recoveryAgentAuthService = recoveryAgentAuthService;
    }

    @PostMapping("/login")
    public BaseResponse login(@Valid @RequestBody RecoveryAgentLoginDto loginDto) {
        return recoveryAgentAuthService.login(loginDto);
    }
}
