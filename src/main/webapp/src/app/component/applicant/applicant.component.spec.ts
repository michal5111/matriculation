import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {ApplicantComponent} from './applicant.component';

describe('ApplicantComponent', () => {
  let component: ApplicantComponent;
  let fixture: ComponentFixture<ApplicantComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ApplicantComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
