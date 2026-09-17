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

  private reimbursements: Reimbursement[] = [];
  readonly dataSource: MatTableDataSource<Reimbursement>;
  selectedReimbursement: Reimbursement | null = null;
  readonly statuses: Reimbursement['status'][] = ['pending', 'approved', 'denied'];
  selectedStatus: Reimbursement['status'] | '' = '';

  displayedColumns = ['reimbursementId', 'description', 'amount', 'type', 'status'];

  constructor(private readonly service: ReimbursementService) {
    this.dataSource = new MatTableDataSource<Reimbursement>([]);
  }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.service.getAllReimbursements().subscribe({
      next: (reimbursements) => {
        this.reimbursements = reimbursements;
        this.applyStatusFilter();
      },
      error: (error) => {
        console.error('Manager reimbursements API error:', error);
      },
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
    this.applyStatusFilter();
  }

  private applyStatusFilter() {
    this.dataSource.data = this.selectedStatus
      ? this.reimbursements.filter((reimbursement) =>
          reimbursement.status.toLowerCase() === this.selectedStatus,
        )
      : this.reimbursements;
  }
}
