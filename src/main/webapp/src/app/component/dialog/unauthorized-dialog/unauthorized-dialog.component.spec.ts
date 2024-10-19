import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {UnauthorizedDialogComponent} from './unauthorized-dialog.component';

describe('UnauthorizedDialogComponent', () => {
  let component: UnauthorizedDialogComponent;
  let fixture: ComponentFixture<UnauthorizedDialogComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [UnauthorizedDialogComponent]
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
