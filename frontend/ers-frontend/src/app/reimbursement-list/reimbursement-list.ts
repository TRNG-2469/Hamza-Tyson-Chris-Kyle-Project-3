import { Component, Output, EventEmitter, ViewChild } from '@angular/core';
import { CurrencyPipe, TitleCasePipe } from '@angular/common';

import { MatTableModule } from '@angular/material/table';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectChange } from '@angular/material/select';

import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ReimbursementService } from '../reimbursement-service';
import { Reimbursement } from '../reimbursement';

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
    private readonly reimbursements: Reimbursement[];

    readonly statuses: Reimbursement['status'][] = ['pending', 'approved', 'denied'];
    selectedStatus: Reimbursement['status'] | '' = '';

    displayedColumns = [
        'id',
        'title',
        'amount',
        'date',
        'status'
    ];

    selectedReimbursement: Reimbursement | null = null;

    constructor(private service: ReimbursementService) {
        this.reimbursements = this.service.getDummyReimbursements();
        this.dataSource = new MatTableDataSource(this.reimbursements);
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
        const status = this.selectedStatus;

        this.dataSource.data = status
            ? this.reimbursements.filter((reimbursement) => reimbursement.status === status)
            : this.reimbursements;
    }


}
