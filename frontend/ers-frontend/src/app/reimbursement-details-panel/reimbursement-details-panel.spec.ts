import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReimbursementDetailsPanel } from './reimbursement-details-panel';

describe('ReimbursementDetailsPanel', () => {
  let component: ReimbursementDetailsPanel;
  let fixture: ComponentFixture<ReimbursementDetailsPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReimbursementDetailsPanel],
    }).compileComponents();

    fixture = TestBed.createComponent(ReimbursementDetailsPanel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
