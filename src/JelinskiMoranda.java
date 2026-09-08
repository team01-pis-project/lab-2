import java.util.Arrays;
import java.util.Locale;

/**
 * Лабораторная работа №2.
 * Модель Джелинского-Моранды (вариант 1).
 */
public class JelinskiMoranda {

    public static void main(String[] args) {
        // ЗАДАЕМ ВХОДНЫЕ ДАННЫЕ
        double[] X = {
                9, 12, 11, 4, 7, 2, 5, 8, 5, 7,
                1, 6, 1, 9, 4, 1, 3, 3, 6, 1,
                11, 33, 7, 91, 2, 1
        };
        int n = X.length;

        // НАХОДИМ СУММУ ИНТЕРВАЛОВ И ВЗВЕШЕННУЮ СУММУ ИНТЕРВАЛОВ
        double sumX = 0.0;
        double sumIX = 0.0;
        for (int i = 0; i < n; i++) {
            sumX  += X[i];
            sumIX += (i + 1) * X[i];
        }

        System.out.println("n = " + n);
        System.out.printf(Locale.US, "ΣX  = %.0f%n", sumX);
        System.out.printf(Locale.US, "ΣiX = %.0f%n%n", sumIX);

        // РЕШАЕМ НЕЛИНЕЙНОЕ УРАВНЕНИЕ ДЛЯ B, ИЩЕМ B > n МЕТОДОМ БИСЕКЦИИ
        double B = findB(n, sumX, sumIX);
        System.out.printf(Locale.US, "Оценка B (вещественная) = %.6f%n", B);

        // БЕРЕМ БЛИЖАЙШЕЕ ЦЕЛОЕ ЗНАЧЕНИЕ ОБЩЕГО ЧИСЛА ОШИБОК B
        int Bint = (int) Math.round(B);
        System.out.println("Принятое целое B = " + Bint);

        // РАССЧИТЫВАЕМ ЗНАЧЕНИЕ КОЭФФИЦИЕНТА ПРОПОРЦИОНАЛЬНОСТИ K
        double denom = (Bint + 1) * sumX - sumIX;
        double K = n / denom;
        System.out.printf(Locale.US, "K = %.9f%n", K);

        //НАХОДИМ СРЕДНЕЕ ВРЕМЯ X_{n+1} ДО ПОЯВЛЕНИЯ n+1 ОШИБКИ
        double Xn1 = 1.0 / (K * (Bint - n));
        System.out.printf(Locale.US, "X_{n+1} = %.3f ч%n", Xn1);

        // НАХОДИМ ВРЕМЯ ДО ОКОНЧАНИЯ ТЕСТИРОВАНИЯ t_k
        double H = 0.0;
        int m = Bint - n;
        for (int i = 1; i <= m; i++) {
            H += 1.0 / i;
        }
        double tk = H / K;
        System.out.printf(Locale.US, "t_k = %.3f ч%n", tk);

        // ВЫВОД РЕЗУЛЬТАТОВ
        System.out.println("\n========== РЕЗУЛЬТАТЫ ==========");
        System.out.println("Общее число ошибок B          = " + Bint);
        System.out.printf(Locale.US, "Коэффициент K                 = %.6f%n", K);
        System.out.printf(Locale.US, "Время до следующей ошибки     = %.3f ч%n", Xn1);
        System.out.printf(Locale.US, "Время до окончания тестирования = %.3f ч%n", tk);
    }

    // МЕТОД findB ДЛЯ РЕШЕНИЯ УРАВНЕНИЯ ДЛЯ B
    private static double findB(int n, double sumX, double sumIX) {
        double left = n + 0.1;          // B > n
        double right = n + 100.0;       // достаточно большой верхний предел
        double eps = 1e-10;

        for (int iter = 0; iter < 200; iter++) {
            double mid = (left + right) / 2.0;
            double fMid = f(mid, n, sumX, sumIX);

            if (Math.abs(fMid) < eps) {
                return mid;
            }
            if (f(left, n, sumX, sumIX) * fMid < 0) {
                right = mid;
            } else {
                left = mid;
            }
        }
        return (left + right) / 2.0;
    }
    // МЕТОД f ДЛЯ ВЫЧИСЛЕНИЯ ЗНАЧЕНИЯ УРАВНЕНИЯ f(B)=0 ПРИ ПОИСКЕ B
    private static double f(double B, int n, double sumX, double sumIX) {
        double leftSum = 0.0;
        for (int i = 1; i <= n; i++) {
            leftSum += 1.0 / (B - i + 1);
        }
        double right = n * sumX / ((B + 1) * sumX - sumIX);
        return leftSum - right;
    }
}