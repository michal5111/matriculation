import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UnauthorizedDialogComponent} from './unauthorized-dialog.component';

describe('UnauthorizedDialogComponent', () => {
  let component: UnauthorizedDialogComponent;
  let fixture: ComponentFixture<UnauthorizedDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UnauthorizedDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnauthorizedDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
