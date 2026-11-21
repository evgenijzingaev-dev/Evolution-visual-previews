# LiteHome Haier — эмулятор и реальные скриншоты

Проект для просмотра облегчённого лаунчера **LiteHome Haier Lite** без доступа к телевизору Haier LE43K6500SA.

## 1. Открыть симулятор в браузере

[Открыть интерфейс с виртуальным пультом](https://htmlpreview.github.io/?https://github.com/evgenijzingaev-dev/Evolution-visual-previews/blob/litehome-haier-emulator/litehome-haier/web/index.html)

В браузере доступны переходы по плиткам, настройки, смена градиента, BACK, OK и кнопка SOURCE. На компьютере используйте стрелки и Enter, на телефоне — кнопки виртуального пульта.

**Важно:** HTML-симулятор демонстрирует внешний вид и навигацию; это не запущенный APK.

## 2. Получить реальные кадры из Android 7.1

[Открыть запуски GitHub Actions](https://github.com/evgenijzingaev-dev/Evolution-visual-previews/actions/workflows/litehome-haier-emulator.yml)

Workflow `LiteHome Haier - Android 7.1 emulator`:
1. Компилирует **нативный Android Java** экран-превью с теми же шестью плитками и настройками.
2. Запускает APK в настоящем Android Emulator (Android 7.1.1 / API 25), имитируя телевизор с соотношением 16:9.
3. Через ADB сохраняет `home-android-7.1.png` и `settings-android-7.1.png`.
4. При успешном запуске загружает APK и скриншоты в **Artifacts → LiteHome-Haier-Android-7.1-captures**.

В GitHub Actions могут возникнуть ошибки загрузки SDK или образа AVD. **Если сборка не завершилась успешно, настоящих кадров ещё нет.** Не путайте с визуальными макетами и генерациями изображений.

## 3. Ограничения

Это **UI-тестовый Android APK**, а не полная сборка пользовательского LiteHome. В настоящем APK взаимодействия с TV-тюнером, Zona и обоями будут зависеть от установленной программы и прошивки Haier. Эмулятор не заменяет проверку на реальном телевизоре.

Все изменения размещены только в ветке `litehome-haier-emulator` репозитория `Evolution-visual-previews`. Основные ветки и файлы Evolution не изменены.
