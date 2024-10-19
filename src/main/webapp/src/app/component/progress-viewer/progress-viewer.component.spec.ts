import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ProgressViewerComponent} from './progress-viewer.component';

describe('ProgressViewerComponent', () => {
  let component: ProgressViewerComponent;
  let fixture: ComponentFixture<ProgressViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgressViewerComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgressViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
