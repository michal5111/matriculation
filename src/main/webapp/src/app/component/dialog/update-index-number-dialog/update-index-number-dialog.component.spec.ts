import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateIndexNumberDialogComponent } from './update-index-number-dialog.component';

describe('UpdateIndexNumberDialogComponent', () => {
  let component: UpdateIndexNumberDialogComponent;
  let fixture: ComponentFixture<UpdateIndexNumberDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UpdateIndexNumberDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateIndexNumberDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
