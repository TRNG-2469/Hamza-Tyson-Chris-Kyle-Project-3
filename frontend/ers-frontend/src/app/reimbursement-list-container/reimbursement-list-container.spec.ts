import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReimbursementListContainer } from './reimbursement-list-container';

describe('ReimbursementListContainer', () => {
  let component: ReimbursementListContainer;
  let fixture: ComponentFixture<ReimbursementListContainer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReimbursementListContainer],
    }).compileComponents();

    fixture = TestBed.createComponent(ReimbursementListContainer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
