import { Component, Output, EventEmitter, ViewChild } from '@angular/core';
import { CurrencyPipe, TitleCasePipe } from '@angular/common';

import { MatTableModule } from '@angular/material/table';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';

import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ReimbursementService } from '../reimbursement-service';
import { Reimbursement } from '../reimbursement';
import { AuthService } from '../core/services/auth.service';

@Component({
    imports: [
        MatTableModule,
        MatSortModule,
        MatExpansionModule,
        MatFormFieldModule,
        MatSelectModule,
        CurrencyPipe,
        TitleCasePipe,
    ],

  selector: 'app-reimbursement-list',
  styleUrl: './reimbursement-list.css',
  templateUrl: './reimbursement-list.html',
})
export class ReimbursementList {

    @Output() reimbursementSelected = new EventEmitter<Reimbursement>();

    @ViewChild(MatSort) sort!: MatSort;

    dataSource: MatTableDataSource<Reimbursement>;

    displayedColumns = [
        'reimbursementId',
        'description',
        'amount',
        'status'
    ];

    selectedReimbursement: Reimbursement | null = null;

    reimbursements: Reimbursement[] = [];
    readonly statuses: Reimbursement['status'][] = ['pending', 'approved', 'denied'];
    selectedStatus: Reimbursement['status'] | '' = '';

    constructor(private service: ReimbursementService, private authService: AuthService) {
        this.dataSource = new MatTableDataSource<Reimbursement>([]);

        const userId = this.authService.userId();
        if (userId !== null) {
            this.service.setUserId(userId);
        }
    }

    ngOnInit(): void {
        if (this.authService.userId() === null) {
            return;
        }

        this.service.getReimbursements().subscribe({
        next: (response: Reimbursement[]) => {
            this.reimbursements = response;
            this.dataSource.data = response;
        },
        error: (error) => {
            console.error('API error:', error);
        }
        });
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
    }

    selectReimbursement(reimbursement: Reimbursement) {
        this.selectedReimbursement = reimbursement;
        this.reimbursementSelected.emit(reimbursement);
    }

    filterByStatus(event: MatSelectChange) {
        this.selectedStatus = event.value as Reimbursement['status'] | '';
        this.dataSource.data = this.selectedStatus
            ? this.reimbursements.filter((reimbursement) =>
                reimbursement.status.toLowerCase() === this.selectedStatus,
            )
            : this.reimbursements;
    }


}
