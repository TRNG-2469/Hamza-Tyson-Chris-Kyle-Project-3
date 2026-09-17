import { Component, Output, EventEmitter, ViewChild } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

import { MatTableModule } from '@angular/material/table';

import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ReimbursementService } from '../reimbursement-service';
import { Reimbursement } from '../reimbursement';

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
        'description',
        'amount',
        'status'
    ];

    selectedReimbursement: Reimbursement | null = null;

    constructor(private service: ReimbursementService) {
          this.dataSource = new MatTableDataSource(
          this.service.getDummyReimbursements()
        );
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
    }

    selectReimbursement(reimbursement: Reimbursement) {
        this.selectedReimbursement = reimbursement;
        this.reimbursementSelected.emit(reimbursement);
    }


}
