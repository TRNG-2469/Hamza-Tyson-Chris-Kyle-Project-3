import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReimbursementSubmission } from './reimbursement-submission';

describe('ReimbursementSubmission', () => {
  let component: ReimbursementSubmission;
  let fixture: ComponentFixture<ReimbursementSubmission>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReimbursementSubmission],
    }).compileComponents();

    fixture = TestBed.createComponent(ReimbursementSubmission);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
