import { Component, Output, EventEmitter, ViewChild } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

import { MatTableModule } from '@angular/material/table';

import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ReimbursementService } from '../reimbursement-service';
import { Reimbursement } from '../reimbursement';
import { AuthService } from '../core/services/auth.service';

@Component({
  imports: [MatTableModule, MatSortModule, CurrencyPipe],

  selector: 'app-reimbursement-list',
  styleUrl: './reimbursement-list.css',
  templateUrl: './reimbursement-list.html',
})
export class ReimbursementList {

    @Output() reimbursementSelected = new EventEmitter<Reimbursement>();

    @ViewChild(MatSort) sort!: MatSort;

    dataSource: MatTableDataSource<Reimbursement>;

    displayedColumns = [
        'id',
        'amount',
        'status'
    ];

    selectedReimbursement: Reimbursement | null = null;

    reimbursements: Reimbursement[] = [];

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


}
