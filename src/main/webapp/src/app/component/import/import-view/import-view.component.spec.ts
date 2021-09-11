import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {ImportViewComponent} from './import-view.component';

describe('ImportViewComponent', () => {
  let component: ImportViewComponent;
  let fixture: ComponentFixture<ImportViewComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ImportViewComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
