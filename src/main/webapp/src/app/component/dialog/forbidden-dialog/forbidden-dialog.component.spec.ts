import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {ForbiddenDialogComponent} from './forbidden-dialog.component';

describe('ForbiddenDialogComponent', () => {
  let component: ForbiddenDialogComponent;
  let fixture: ComponentFixture<ForbiddenDialogComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ForbiddenDialogComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ForbiddenDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
