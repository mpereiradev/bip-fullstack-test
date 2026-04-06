import { Directive, ElementRef, HostListener, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Directive({
  selector: 'input[appCurrencyMask]',
  standalone: true,
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => CurrencyMaskDirective),
    multi: true,
  }],
})
export class CurrencyMaskDirective implements ControlValueAccessor {

  private onChange: (value: number | null) => void = () => {};
  private onTouched: () => void = () => {};

  constructor(private el: ElementRef<HTMLInputElement>) {}

  writeValue(value: number | null): void {
    this.el.nativeElement.value = value != null && value > 0 ? this.format(value) : '';
  }

  registerOnChange(fn: (value: number | null) => void): void { this.onChange = fn; }
  registerOnTouched(fn: () => void): void { this.onTouched = fn; }
  setDisabledState(isDisabled: boolean): void { this.el.nativeElement.disabled = isDisabled; }

  @HostListener('input', ['$event'])
  onInput(event: Event): void {
    const digits = (event.target as HTMLInputElement).value.replace(/\D/g, '');
    if (!digits) {
      this.el.nativeElement.value = '';
      this.onChange(null);
      return;
    }
    const numeric = parseInt(digits, 10) / 100;
    this.el.nativeElement.value = this.format(numeric);
    this.onChange(numeric);
  }

  @HostListener('blur')
  onBlur(): void { this.onTouched(); }

  private format(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 2,
    }).format(value);
  }
}
