import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {ImportSetupComponent} from './import-setup.component';

describe('ImportSetupComponent', () => {
  let component: ImportSetupComponent;
  let fixture: ComponentFixture<ImportSetupComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ImportSetupComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportSetupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
