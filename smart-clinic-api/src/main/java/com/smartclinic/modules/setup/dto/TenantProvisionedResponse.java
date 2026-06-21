package com.smartclinic.modules.setup.dto;

/**
 * Returned immediately after tenant provisioning completes.
 * Contains everything the new hospital admin needs to log in for the first time.
 *
 * SECURITY: temporaryPassword is shown exactly once and never stored in plaintext.
 * The admin must change it on first login (enforced in Phase 2).
 */
public record TenantProvisionedResponse(
        TenantResponse tenant,
        String         adminEmail,
        String         temporaryPassword,
        String         loginUrl,
        String         message
) {}
