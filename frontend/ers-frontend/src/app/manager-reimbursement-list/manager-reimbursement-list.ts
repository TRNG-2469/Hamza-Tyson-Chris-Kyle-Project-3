import { CurrencyPipe, TitleCasePipe } from '@angular/common';
import { Component, EventEmitter, Output, ViewChild } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { ReimbursementService } from '../reimbursement-service';
import { Reimbursement } from '../reimbursement';

@Component({
  imports: [
    CurrencyPipe,
    TitleCasePipe,
    MatExpansionModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSortModule,
    MatTableModule,
  ],
  selector: 'app-manager-reimbursement-list',
  styleUrl: './manager-reimbursement-list.css',
  templateUrl: './manager-reimbursement-list.html',
})
export class ManagerReimbursementList {
  @Output() reimbursementSelected = new EventEmitter<Reimbursement>();
  @ViewChild(MatSort) sort!: MatSort;

  private readonly reimbursements: Reimbursement[];
  readonly dataSource: MatTableDataSource<Reimbursement>;
  selectedDepartment = '';
  selectedReimbursement: Reimbursement | null = null;

  displayedColumns = ['id', 'title', 'amount', 'status'];

  constructor(private readonly service: ReimbursementService) {
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
}
