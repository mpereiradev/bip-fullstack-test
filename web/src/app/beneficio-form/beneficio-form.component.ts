import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BeneficioService } from '../shared/services/beneficio.service';
import { CurrencyMaskDirective } from '../shared/directives/currency-mask.directive';

@Component({
  selector: 'app-beneficio-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, CurrencyMaskDirective],
  templateUrl: './beneficio-form.component.html',
})
export class BeneficioFormComponent implements OnInit {

  form: FormGroup;
  editando = signal(false);
  salvando = signal(false);
  erro     = signal<string | null>(null);

  private id?: number;

  constructor(
    private fb: FormBuilder,
    private service: BeneficioService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      nome:     ['', [Validators.required, Validators.maxLength(100)]],
      descricao: ['', Validators.maxLength(255)],
      valor:    [null, [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    if (this.id) {
      this.editando.set(true);
      this.service.buscarPorId(this.id).subscribe({
        next:  b    => this.form.patchValue({ nome: b.nome, descricao: b.descricao, valor: b.valor }),
        error: ()   => this.erro.set('Benefício não encontrado.')
      });
    }
  }

  salvar(): void {
    if (this.form.invalid) return;
    this.salvando.set(true);
    this.erro.set(null);

    const op$ = this.id
      ? this.service.atualizar(this.id, this.form.value)
      : this.service.criar(this.form.value);

    op$.subscribe({
      next:  () => this.router.navigate(['/beneficios']),
      error: err => { this.erro.set(err?.error?.message ?? 'Erro ao salvar.'); this.salvando.set(false); }
    });
  }

  isInvalid(campo: string): boolean {
    const ctrl = this.form.get(campo);
    return !!(ctrl?.invalid && ctrl?.touched);
  }
}
