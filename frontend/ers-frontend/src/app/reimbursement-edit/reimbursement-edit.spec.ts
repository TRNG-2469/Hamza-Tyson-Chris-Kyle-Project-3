import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReimbursementEdit } from './reimbursement-edit';

describe('ReimbursementEdit', () => {
  let component: ReimbursementEdit;
  let fixture: ComponentFixture<ReimbursementEdit>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReimbursementEdit]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ReimbursementEdit);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
