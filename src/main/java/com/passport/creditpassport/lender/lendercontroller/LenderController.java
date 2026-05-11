package com.passport.creditpassport.lender.lendercontroller;


import com.passport.creditpassport.creditpassport.CreditPassport;
import com.passport.creditpassport.creditpassport.CreditPassportQueryPort;
import com.passport.creditpassport.lender.lenderdto.CreditPassportRequest;
import com.passport.creditpassport.lender.lenderdto.LenderRequest;
import com.passport.creditpassport.lender.lenderdto.LenderResponse;
import com.passport.creditpassport.lender.lendermodel.Lender;
import com.passport.creditpassport.lender.lenderservice.LenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lender", description = "Lender registration endpoint")
@RestController
@RequestMapping("/api/lender")
@RequiredArgsConstructor
public class LenderController {

    private final LenderService lenderService;
    private final CreditPassportQueryPort creditPassportQueryPort;

    @PostMapping("/register")
    public ResponseEntity<LenderResponse> register(@Valid @RequestBody LenderRequest request) {
        LenderResponse response = lenderService.registerLender(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Fetch credit passport by national ID for authenticated lender")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "apiKeyAuth")
    @PostMapping("/credit-passport")
    public ResponseEntity<CreditPassport> getCreditPassport(
        @Valid @RequestBody CreditPassportRequest req,
        HttpServletRequest httpRequest) {

        // 1. Retrieve the Lender placed on the request by ApiKeyAuthFilter.
        //    The filter guarantees this attribute is non-null if we reach here.
        Lender lender = (Lender) httpRequest.getAttribute("authenticatedLender");

        // 2. Verify the cbkLicenseNo in the body belongs to the authenticated lender.
        //    Guards against a valid key being used with a different licence number.
        if (!lender.getCbkLicenseNo().equals(req.cbkLicenseNo())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 3. Fetch and return the credit passport.
        CreditPassport passport = creditPassportQueryPort.getCreditPassport(req.nationalId());
        return ResponseEntity.ok(passport);
    }

    @Operation(summary = "Lender API health endpoint")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "apiKeyAuth")
    @GetMapping("/health")
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }
}
