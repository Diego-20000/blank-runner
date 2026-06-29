# Android Studio - Flujo de Trabajo

## ¿Qué hace automático Android Studio?

### ✅ AUTOMÁTICO - Gradle Sync
```
Cuando abres Android Studio:
1. Detecta cambios en build.gradle
2. Descarga dependencias automáticamente
3. Sincroniza el proyecto
4. Tarda ~30 segundos (primera vez), después más rápido
```

**Puedes ver:** `Gradle sync in progress...` en la barra inferior

### ❌ NO AUTOMÁTICO - Git Pull (Actualizar código)
```
Android Studio NO actualiza el código git automáticamente.
Solo lee lo que ya está en tu máquina local.
```

---

## Flujo Correcto en Android Studio

### Opción A: Actualizar ANTES de abrir Android Studio
```bash
# Terminal (ANTES de abrir Android Studio)
cd ~/blank-runner
git pull origin claude/blank-runner-platformer-setup-ndyy7x

# Luego abrir Android Studio
open -a "Android Studio" .
# o solo abrirlo desde el Finder
```

### Opción B: Actualizar DENTRO de Android Studio
```
1. Android Studio abierto → VCS (menu)
2. Git → Pull
3. Selecciona rama: claude/blank-runner-platformer-setup-ndyy7x
4. Click "Pull"

Automáticamente:
- Android Studio hace Gradle sync
- Listo para compilar
```

### Opción C: Comando en Terminal (Más rápido)
```bash
# Si Android Studio está abierto:
# Menu: VCS → Git → Pull

# O desde terminal:
cd ~/blank-runner
git pull
# Android Studio detecta cambios y hace sync automáticamente
```

---

## ¿Necesito Actualizar Cada Vez?

### 📌 DEPENDE de dónde hagas cambios:

#### Escenario 1: Solo editas localmente (tú solamente)
```
NO necesitas git pull porque:
- Estás editando en tu máquina
- Los cambios están locales
- No hay actualizaciones remotas

Flujo:
Edit código → Compilar → Listo
```

#### Escenario 2: Cambios en remote (GitHub)
```
SÍ necesitas git pull porque:
- Alguien hizo push a la rama
- Hay cambios nuevos en el servidor
- Tu máquina local está desactualizada

Flujo:
git pull → Compilar → Listo
```

#### Escenario 3: Trabajas con otros (equipo)
```
SIEMPRE git pull antes de compilar:
- Otros pueden haber actualizado código
- Pueden haber conflictos
- Mejor prevenir que resolver después

Flujo:
git pull → Resolver conflictos si hay → Compilar
```

---

## Recomendación: Automatizar Pull

### Opción 1: Alias en Terminal
```bash
# Agregar a ~/.bashrc o ~/.zshrc:
alias pullstudio='cd ~/blank-runner && git pull && echo "✓ Actualizado"'

# Usar:
pullstudio
# Luego abre Android Studio
```

### Opción 2: Gradle Task Personalizado
```gradle
// En android/build.gradle, agregar:
tasks.register('updateRepo') {
    doFirst {
        exec {
            commandLine 'git', 'pull'
        }
    }
}
```

Luego en Android Studio:
- Gradle Tasks → updateRepo (ejecuta git pull)

### Opción 3: Hook de Git (Automático)
```bash
# Crear hook que actualiza automáticamente
mkdir -p .git/hooks
cat > .git/hooks/post-checkout << 'EOF'
#!/bin/sh
echo "Ejecutando Gradle sync después de git pull..."
gradle sync
EOF
chmod +x .git/hooks/post-checkout
```

---

## Checklist: Antes de Compilar en Android Studio

```
☐ Abriste Android Studio?
   └─ Espera "Gradle sync in progress..." en la barra inferior

☐ Necesitas actualizaciones del repo?
   └─ Sí: VCS → Git → Pull
   └─ No: Continúa

☐ ¿Hay cambios sin commitear?
   └─ Sí: Commit primero (VCS → Commit)
   └─ No: Continúa

☐ Estás en la rama correcta?
   └─ Verifica en la esquina inferior derecha: 
      "claude/blank-runner-platformer-setup-ndyy7x"
   └─ Si no: VCS → Git → Branches → Switch to...

☐ Ahora puedes compilar:
   Build → Make Project (Ctrl+F9)
   o
   Run → Run 'android' (Shift+F10)
```

---

## El Flujo MÁS RÁPIDO (Recomendado)

### Opción A: Terminal + Android Studio (Mejor)
```bash
# 1. Terminal - Actualizar repo
cd ~/blank-runner
git pull

# 2. Android Studio - Compilar
# Abre Android Studio (o ya está abierto)
# Automáticamente hace Gradle sync
# Shift+F10 para compilar y ejecutar

# ✓ Listo en 2 minutos
```

### Opción B: Solo Android Studio
```
1. Android Studio abierto
2. VCS → Git → Pull
3. Espera Gradle sync (~30 seg)
4. Shift+F10 para compilar
5. ✓ Listo en 2 minutos
```

---

## Qué NO Necesitas Hacer

```
❌ Limpiar caché manualmente cada vez
   → Android Studio solo limpia si es necesario

❌ Reimportar el proyecto cada vez
   → Android Studio lo detecta automáticamente

❌ Hacer "Invalidate Caches" cada compilación
   → Solo si algo anda raro

❌ Cerrar y abrir Android Studio cada vez
   → Mantén una sesión abierta
```

---

## Cuando SÍ Necesitas Invalidate Caches

Si algo anda raro:
```
File → Invalidate Caches → Invalidate and Restart
(Esto borra cache y reinicia Android Studio)

Úsalo SOLO si:
- La compilación falla sin razón
- Los cambios no se reflejan
- Las clases no se resuelven
```

---

## Resumen Final

### Para Tu Flujo:

1. **Abre Android Studio** (primera vez del día)
   - Automáticamente: Gradle sync

2. **Cada vez que vayas a compilar:**
   ```bash
   # En terminal (2 segundos)
   git pull
   # O en Android Studio: VCS → Git → Pull
   ```

3. **Compila:**
   ```
   Shift+F10 (Run)
   ```

4. **Ver en dispositivo**
   - Automáticamente abre la app

### Variables:

- **Si trabajas solo:** Pull 1 vez al día
- **Si trabajas en equipo:** Pull antes de cada compilación
- **Si solo editas localmente:** No necesitas pull

---

## Atajo Super Rápido (Terminal Integrada)

En Android Studio puedes usar terminal integrada:
```
View → Tool Windows → Terminal
```

Luego:
```bash
git pull  # En la terminal integrada
# Android Studio detecta cambios y sincroniza automáticamente
```

---

## Problemas Comunes

### "Changes in branch not showing"
```
Causa: No hiciste git pull
Solución: VCS → Git → Pull
```

### "Gradle sync keeps failing"
```
Causa: Conflictos después de pull
Solución: VCS → Git → Resolve Conflicts → Compile
```

### "Old version of code is running"
```
Causa: No hiciste pull o caché stale
Solución: 
1. git pull
2. Build → Clean Project
3. Run
```

---

## La Verdad Sobre Auto-Sync

```
✅ Gradle SYNC es automático
   (Gradle detecta cambios en build.gradle)

❌ Git PULL no es automático
   (Tienes que hacer tú: git pull o VCS → Pull)

✅ Build CACHE es automático
   (Solo compila lo que cambió)
```

---

## MI RECOMENDACIÓN

### Para máxima velocidad:

```bash
# En tu terminal favorita:
cd ~/blank-runner

# Alias útil (agregar a ~/.bashrc)
alias dev='git pull && open -a "Android Studio" .'

# Usar cada vez que quieras desarrollar:
dev

# Luego en Android Studio:
# Shift+F10 para compilar
```

**Así en 2 minutos tienes código actualizado + compilado.**
