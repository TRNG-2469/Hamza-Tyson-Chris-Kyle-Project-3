import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReimbursementList } from './reimbursement-list';

describe('ReimbursementList', () => {
  let component: ReimbursementList;
  let fixture: ComponentFixture<ReimbursementList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReimbursementList],
    }).compileComponents();

    fixture = TestBed.createComponent(ReimbursementList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('filters reimbursements by status', () => {
    component.filterByStatus({ value: 'approved' } as never);

    expect(component.dataSource.data.every((reimbursement) => reimbursement.status === 'approved')).toBe(true);
    expect(component.dataSource.data).toHaveLength(2);
  });

  it('shows all reimbursements when the status filter is cleared', () => {
    component.filterByStatus({ value: 'approved' } as never);
    component.filterByStatus({ value: '' } as never);

    expect(component.dataSource.data).toHaveLength(4);
  });
});
