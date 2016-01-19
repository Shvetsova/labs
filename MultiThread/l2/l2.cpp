// l2.cpp: определяет точку входа для консольного приложения.
//

#include <stdio.h>  
#include <iostream>  
#include <time.h> 
#include <fstream>
#include <math.h>
#include <mpi.h>
#include <stdlib.h>
#include <sstream> 
#include <stdexcept>

#define ARG_COUNT 5

# define EXIT_APP(RANK,MESS) if((RANK) == 0) { \
		      std::cout << (MESS) << std::endl; \
		      } \
		      MPI_Finalize();\
		      return 0;

void Jacobi(int M, double** A, double* F, double* X, double eps) {
	int rank, size;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);// узнаем индетификатор процесса
	MPI_Comm_size(MPI_COMM_WORLD, &size);// узнаем количество процессов

	double* TempX = (double *)calloc(sizeof(double), M); //массив промежуточных результатов
	double norm = 1;// передается из всех процессов максимальная TempNorm
	double TempNorm = 1;// рассчитывается в каждом процессе

	double mtime = 0;
	if (rank == 0) {
		mtime = MPI_Wtime();// получаем текущее значение времени для первого процесса
	}

	do {
		MPI_Bcast(X, M, MPI_DOUBLE, 0, MPI_COMM_WORLD); // рассылаем всем процессам значение текущего приближения
		for (int i = rank; i < M; i += size) {// каждый процесс считает свою часть цикла
			TempX[i] = F[i];
			for (int g = 0; g < M; g++) {
				if (i != g)
					TempX[i] -= A[i][g] * X[g];
			}
			TempX[i] /= A[i][i];
			//рассчет промежуточного решения
		}
		MPI_Barrier(MPI_COMM_WORLD);// ждем пока процессы дойдут до этой точки
		TempNorm = abs(X[0] - TempX[0]); // считаем TempNorm по модулю
		for (int h = rank; h < M; h += size) {// каждый процесс бежит со своим шагом, ищем максимальный TempNorm
			if (abs(X[h] - TempX[h]) > TempNorm)
				TempNorm = abs(X[h] - TempX[h]);
		}
		MPI_Barrier(MPI_COMM_WORLD);// ждем пока процессы дойдут до этой точки
		MPI_Reduce(&TempNorm, &norm, 1, MPI_DOUBLE, MPI_MAX, 0, MPI_COMM_WORLD);// шлем главному процессу максимальную TempNorm и записываем в norm
		MPI_Reduce(TempX, X, M, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);// собираем из всех промежуточных приближений ответ

	} while (norm > eps);//пока норм больше точности
	free(TempX);// освободаем tempx

	if (rank == 0) {// если главный процесс
		mtime = MPI_Wtime() - mtime;
		std::cout << "calculation time : " << mtime << std::endl; // выводим значения потраченного времени
	}

}

int Converge(int M, double** A) { // рассчет сходимсти
	int rank, size;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank); // узнаем индетификатор процесса
	MPI_Comm_size(MPI_COMM_WORLD, &size);// узнаем количество процессов

	for (int i = rank; i < M; i += size) {    // бежим с шагом зависящим от индетификатора процесса и количества процессов
		double sum = 0.0;
		for (int j = 0; j < M; j++) {
			if (i != j) {
				sum += abs(A[i][j]); // сумма модулей недиагональных элементов
			}
		}
		if (abs(A[i][i]) < sum) {// если сумма модулей недиагональных элементов больше модуля диагонального элемента
			throw std::logic_error("not converge");
		}
	}
	MPI_Barrier(MPI_COMM_WORLD);
}
int main(int argc, char **argv) {

	int rank;
	MPI_Init(&argc, &argv); //инициализация mpi
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);// получение индетефикатора процесса

										 /*
										 *  argv[1] - input file coefficient matrix
										 *  argv[2] - input file initial approximation
										 *  argv[3] - accuracy value
										 *  argv[4] - output file
										 */

	if (argc != ARG_COUNT) {
		EXIT_APP(rank, " incorrect number of arguments ");
	}

	double eps = atof(argv[3]); // погрешность 

	if (eps <= 0 || eps >= 1) {// если не от 0 до 1
		EXIT_APP(rank, " incorrect eps ");
	}

	std::ifstream fmatrix(argv[1]);
	std::ifstream fapprox(argv[2]);
	std::ofstream fout(argv[4]);

	if (!fmatrix.is_open() || !fapprox.is_open() || !fout.is_open()) {// если не открылись файлы
		EXIT_APP(rank, " could not open file ");
	}

	// --> matrix file read
	int M;// количество строк
	int N;//  количество столбцов
	double** A;//матрица коэффициентов
	double* F;//свободные члены
	

	std::string line;
	std::getline(fmatrix, line);
	std::istringstream iss(line);
	if (!(iss >> M >> N)) {
		EXIT_APP(rank, " matrix file could not get M and N ");
	}
	if (N != M + 1) { // если столбцов не на один больше строк
		EXIT_APP(rank, " matrix incorrect N != M + 1 ");
	}

	A = (double**)malloc(sizeof(double*) * M);// выделение памяти под указатели на массивы
	F = (double*)malloc(sizeof(double) * M);// выделение памяти под массив

	for (int i = 0; i < M; i++) {
		line = "";// задаем строку пустой
		std::getline(fmatrix, line);// читаем из входного потока с матрицей
		if (line.size() < 1) {// если строка меньше 1 символа
			EXIT_APP(rank, " matrix line incorrect ");
		}
		A[i] = (double*)malloc(sizeof(double) * M);// выделяем память под массив - строку

		std::istringstream issl(line);
		for (int j = 0; j < M; j++) {// бежим по количеству элементов в столбце
			if (!(issl >> A[i][j])) {// если нет элемента в столбце 
				EXIT_APP(rank, " matrix line incorrect ");
			}
		}

		if (!(issl >> F[i])) {// последний элемент в строке записывается в f
			EXIT_APP(rank, " matrix line incorrect ");
		}
	}
	// <-- matrix file read

	// --> approximation
	double* X;// столбец приближений
	int aM;// количество строк
	line = "";
	std::getline(fapprox, line);
	std::istringstream aiss(line);

	if (!(aiss >> aM)) {
		EXIT_APP(rank, " approximation file could not get M ");
	}
	if (M != aM) {// если количество строк в матрице не равно количеству строк в приближении
		EXIT_APP(rank, " approximation file M does not match matrix M ");
	}

	X = (double*)calloc(sizeof(double), M);// выделяем память под массив приближений

	for (int i = 0; i < M; i++) {//
		line = "";
		std::getline(fapprox, line);
		if (line.size() < 1) {
			EXIT_APP(rank, " approximation line incorrect ");
		}
		std::istringstream aissl(line);
		if (!(aissl >> X[i])) {
			EXIT_APP(rank, " approximation line incorrect ");
		}
	}
	// <-- approximation

	MPI_Barrier(MPI_COMM_WORLD); //  дождаться пока все процессы дойдут до этой точки
	try {

		Converge(M, A);// проверка сходимости
		Jacobi(M, A, F, X, eps); // вычисление слау
	
	}
	catch (std::logic_error e) {
		EXIT_APP(rank, e.what());
	}
	
	// --> output  

	if (rank == 0) {
		fout << M << std::endl;
		for (int i = 0; i < M; i++) {
			fout << X[i] << std::endl;
		}
	}

	// <-- output

	fmatrix.close();
	fapprox.close();
	fout.close();

	EXIT_APP(rank, "Succeeded")
}
