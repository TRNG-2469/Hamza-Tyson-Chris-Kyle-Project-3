import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ReimbursementList } from '../reimbursement-list/reimbursement-list';
import { ReimbursementDetailsPanel } from '../reimbursement-details-panel/reimbursement-details-panel';
import { Reimbursement } from '../reimbursement';
import { ReimbursementEdit } from '../reimbursement-edit/reimbursement-edit';
import { AuthService } from '../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
    imports: [ReimbursementList, ReimbursementDetailsPanel, ReimbursementEdit, RouterLink],
  selector: 'app-reimbursement-list-container',
  styleUrl: './reimbursement-list-container.css',
  templateUrl: './reimbursement-list-container.html',
})
export class ReimbursementListContainer {
    authService: AuthService = inject(AuthService);
    private readonly router = inject(Router)

    selectedReimbursement: Reimbursement | null = null;
    startedEdit: boolean = false;

    handleSelectReimbursement(reimbursement: Reimbursement) {
        this.selectedReimbursement = reimbursement;
    }

    // logout method
    onLogout(): void {
        this.authService.logout();
        this.router.navigateByUrl('/login'); 

    }
}
