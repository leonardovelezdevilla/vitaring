"""Recorre VitaRing en el emulador: registro, pantallas, recordatorio, edición y reinicio.
Deja capturas y un resumen en capturas/. Sale con código 1 si alguna verificación falla."""
import os
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

PKG = "pe.vitaring.app"
APK = "app/build/outputs/apk/debug/app-debug.apk"
SAL = "capturas"
os.makedirs(SAL, exist_ok=True)

resultados = []
n_captura = [0]


def adb(*args):
    return subprocess.run(["adb", *args], capture_output=True, text=True).stdout


def sh(cmd):
    return adb("shell", cmd)


def log(msg):
    print(msg, flush=True)


def captura(nombre):
    n_captura[0] += 1
    datos = subprocess.run(["adb", "exec-out", "screencap", "-p"], capture_output=True).stdout
    with open(f"{SAL}/{n_captura[0]:02d}-{nombre}.png", "wb") as f:
        f.write(datos)


def nodos():
    for _ in range(5):
        sh("uiautomator dump /sdcard/u.xml")
        xml = adb("shell", "cat", "/sdcard/u.xml")
        i = xml.find("<?xml")
        if i >= 0:
            try:
                raiz = ET.fromstring(xml[i:].strip())
                return list(raiz.iter("node"))
            except ET.ParseError:
                pass
        time.sleep(1)
    return []


def centro(nodo):
    x1, y1, x2, y2 = map(int, re.findall(r"\d+", nodo.get("bounds")))
    return (x1 + x2) // 2, (y1 + y2) // 2


def buscar(texto, exacto=False, ultimo=False):
    hallados = []
    for nd in nodos():
        for valor in (nd.get("text", ""), nd.get("content-desc", "")):
            if (valor == texto) if exacto else (texto in valor):
                hallados.append(nd)
                break
    if not hallados:
        return None
    hallados.sort(key=lambda nd: centro(nd)[1])
    return hallados[-1] if ultimo else hallados[0]


def pantalla():
    m = re.search(r"Physical size: (\d+)x(\d+)", sh("wm size"))
    return (int(m.group(1)), int(m.group(2))) if m else (1080, 2400)


ANCHO, ALTO = 1080, 2400


def deslizar_arriba():
    sh(f"input swipe {ANCHO // 2} {int(ALTO * 0.7)} {ANCHO // 2} {int(ALTO * 0.3)} 400")
    time.sleep(1)


def deslizar_abajo():
    sh(f"input swipe {ANCHO // 2} {int(ALTO * 0.3)} {ANCHO // 2} {int(ALTO * 0.75)} 300")
    time.sleep(0.6)


def al_inicio():
    for _ in range(6):
        deslizar_abajo()


def esperar(texto, segundos=10, scroll=False, exacto=False, ultimo=False):
    fin = time.time() + segundos
    deslizamientos = 0
    while time.time() < fin or (scroll and deslizamientos < 8):
        nd = buscar(texto, exacto, ultimo)
        if nd is not None:
            return nd
        if scroll:
            deslizar_arriba()
            deslizamientos += 1
        else:
            time.sleep(1)
    return None


def tocar(texto, scroll=True, exacto=False, ultimo=False):
    nd = esperar(texto, 6, scroll, exacto, ultimo)
    if nd is None:
        log(f"  !! no encontré '{texto}' para tocar")
        return False
    x, y = centro(nd)
    sh(f"input tap {x} {y}")
    time.sleep(1.2)
    return True


def verificar(nombre, ok):
    resultados.append((nombre, bool(ok)))
    log(("  OK   " if ok else "  FALLA ") + nombre)


def hay(texto, scroll=False, segundos=6):
    return esperar(texto, segundos, scroll) is not None


def teclado_visible():
    return re.search(r"(mInputShown|mIsInputViewShown|isInputViewShown)=true", sh("dumpsys input_method")) is not None


def ocultar_teclado():
    if teclado_visible():
        sh("input keyevent 4")
        time.sleep(0.8)


def escribir(etiqueta, valor):
    if tocar(etiqueta, scroll=False):
        sh(f"input text {valor}")
        time.sleep(0.6)
        ocultar_teclado()


def abrir_app():
    sh(f"am start -W -n {PKG}/.MainActivity")
    time.sleep(3)


def main():
    global ANCHO, ALTO
    sh("settings put global window_animation_scale 0")
    sh("settings put global transition_animation_scale 0")
    sh("settings put global animator_duration_scale 0")
    ANCHO, ALTO = pantalla()
    log(f"Pantalla {ANCHO}x{ALTO}")

    log(adb("install", "-r", "-g", APK))
    sh("logcat -c")
    abrir_app()

    log("== Bienvenida")
    verificar("Bienvenida se muestra la primera vez", hay("VITARING"))
    captura("bienvenida")
    tocar("Comenzar")

    log("== Registro (validación)")
    verificar("Pantalla de perfil abre tras 'Comenzar'", hay("Cuéntanos de ti"))
    captura("perfil-vacio")
    tocar("Guardar y continuar")
    verificar("Valida actividad vacía", hay("Elige tu actividad diaria", scroll=True))
    al_inicio()
    verificar("Valida nombre vacío", hay("Escribe tu nombre"))
    verificar("Valida edad", hay("Edad entre 10 y 100"))
    captura("perfil-errores")

    log("== Registro (datos)")
    escribir("Nombre", "Ana")
    escribir("Edad", "20")
    escribir("Peso", "60")
    escribir("Talla", "165")
    captura("perfil-lleno")
    tocar("Sedentario")
    captura("perfil-actividad")
    tocar("Guardar y continuar")
    time.sleep(2)
    if hay("Permitir", segundos=2):  # por si el sistema pide el permiso de notificaciones
        tocar("Permitir", scroll=False)

    log("== Inicio")
    verificar("Inicio saluda con el nombre", hay("Hola, Ana", segundos=10))
    verificar("Meta de pasos según actividad (6 000)", hay("de 6 000", scroll=True))
    verificar("Meta de sueño en Inicio", hay("Meta 8 h", scroll=True))
    al_inicio()
    captura("inicio")
    verificar("Recordatorio programado al guardar", "recordatorio_movimiento" in sh(f"dumpsys jobscheduler {PKG}") or
               "RecordatorioWorker" in sh(f"dumpsys jobscheduler {PKG}") or
               "activo\" value=\"true" in sh(f"run-as {PKG} cat shared_prefs/recordatorio.xml"))

    log("== Ritmo")
    tocar("Corazón", scroll=False, exacto=True, ultimo=True)
    verificar("Pulso máximo 220 − edad = 200", hay("200 lpm", scroll=True))
    verificar("Fórmula visible", hay("220 − 20 años"))
    captura("ritmo-pulso-maximo")
    al_inicio()
    tocar("Medir ahora")
    time.sleep(2)
    captura("resultado")
    sh("input keyevent 4")
    time.sleep(1)

    log("== Actividad")
    tocar("Actividad", scroll=False, exacto=True, ultimo=True)
    verificar("Meta de pasos en Actividad", hay("de 6 000 pasos"))
    captura("actividad")
    verificar("Tarjeta de recordatorio", hay("Recordatorio de movimiento", scroll=True))
    verificar("Intervalo según actividad (1 h)", hay("Cada 1 h"))
    captura("actividad-recordatorio")
    tocar("Probar ahora")
    time.sleep(2)
    verificar("'Probar ahora' muestra la notificación", "Hora de moverte" in sh("dumpsys notification --noredact"))
    sh("cmd statusbar expand-notifications")
    time.sleep(2)
    captura("notificacion")
    sh("cmd statusbar collapse")
    time.sleep(1)
    switch = next((nd for nd in nodos() if nd.get("checkable") == "true"), None)
    if switch is not None:
        x, y = centro(switch)
        sh(f"input tap {x} {y}")
        time.sleep(1.5)
        verificar("Switch desactiva el recordatorio", hay("Desactivado"))
        captura("recordatorio-desactivado")
        sh(f"input tap {x} {y}")
        time.sleep(1.5)
        verificar("Switch vuelve a activarlo", hay("Cada 1 h"))
    else:
        verificar("Switch encontrado", False)

    log("== Bienestar")
    tocar("Bienestar", scroll=False, exacto=True, ultimo=True)
    verificar("Gráfico de sueño semanal", hay("Horas de sueño esta semana", scroll=True))
    deslizar_arriba()
    captura("bienestar-sueno")
    verificar("Meta de sueño marcada", hay("Meta 8 h"))

    log("== Mi anillo")
    tocar("Anillo", scroll=False, exacto=True, ultimo=True)
    captura("anillo")

    log("== Editar perfil desde el avatar")
    tocar("Inicio", scroll=False, exacto=True, ultimo=True)
    al_inicio()
    tocar("A", scroll=False, exacto=True)
    verificar("Avatar abre 'Mi perfil'", hay("Mi perfil"))
    captura("editar-perfil")
    if tocar("Ana", scroll=False, exacto=True):
        sh("input keyevent 123")  # fin del texto
        sh("input keyevent 67 67 67")
        sh("input text Odet")
        time.sleep(0.6)
        ocultar_teclado()
    tocar("Guardar cambios")
    verificar("Nombre editado en Inicio", hay("Hola, Odet", segundos=10))

    log("== Reabrir la app")
    sh(f"am force-stop {PKG}")
    time.sleep(1)
    abrir_app()
    verificar("Con perfil abre directo en Inicio", hay("Hola, Odet", segundos=10))
    captura("reabrir")

    crash = adb("logcat", "-d", "-b", "crash")
    verificar("Sin cierres inesperados (crash)", PKG not in crash)
    if PKG in crash:
        with open(f"{SAL}/crash.txt", "w") as f:
            f.write(crash)

    ok = sum(1 for _, b in resultados if b)
    lineas = [f"# Prueba en emulador: {ok}/{len(resultados)} OK", ""]
    lineas += [f"- {'✅' if b else '❌'} {nombre}" for nombre, b in resultados]
    with open(f"{SAL}/RESULTADO.md", "w") as f:
        f.write("\n".join(lineas) + "\n")
    log("\n".join(lineas))
    sys.exit(0 if ok == len(resultados) else 1)


if __name__ == "__main__":
    main()
