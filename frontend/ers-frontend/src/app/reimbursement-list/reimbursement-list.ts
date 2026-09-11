import { Component } from '@angular/core';
import { Reimbursementervice } from '../reimbursement-service';

@Component({
  imports: [],
  selector: 'app-reimbursement-list',
  styleUrl: './reimbursement-list.css',
  templateUrl: './reimbursement-list.html',
})
export class ReimbursementList {

  currentList: Reimbursement[];

  constructor(private service: Reimbursementervice) { }

  ngOnInit: void {
    
    this.currentList = service.getDummyReimbursements();
  }


}
