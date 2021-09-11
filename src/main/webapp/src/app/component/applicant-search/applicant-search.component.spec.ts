import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {ApplicantSearchComponent} from './applicant-search.component';

describe('ApplicantSearchComponent', () => {
  let component: ApplicantSearchComponent;
  let fixture: ComponentFixture<ApplicantSearchComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ApplicantSearchComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicantSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
