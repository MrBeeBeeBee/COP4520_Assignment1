/*
Benjamin Kuftic
COP4520 Spring 2024
Assignment 1
Counting Primes With Threads
*/

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

// Uses threads on specific ranges of numbers
class PrimeThread extends Thread {
	private final int start;
	private final int end;
	private final BitSet primes;

	public PrimeThread(int start, int end, BitSet primes) {
		this.start = start;
		this.end = end;
		this.primes = primes;
	}

	// Uses Sieve of Eratosthenes to remove non-prime numbers
	public void run() {
		for (int i = 2; i * i <= end; i++)
		{
			if (primes.get(i))
			{
				for (int j = Math.max(i * i, (start + i - 1) / i * i); j <= end; j += i)
				{
					primes.clear(j);
				}
			}
		}
	}
}

public class Assignment1 {
	private static final int NUM_THREADS = 8;
	private static final int UPPER_LIMIT = 100000000;

	public static void main(String[] args)
	{
		// Records the starting time
		long startTime = System.currentTimeMillis();

		// Assumes that all numbers are prime until proven otherwise
		BitSet primes = new BitSet(UPPER_LIMIT + 1);
		primes.set(2, UPPER_LIMIT + 1);

		List<PrimeThread> threads = new ArrayList<>();

		// Initializes the 8 threads and assigns them a chunk of numbers to operate through
		for (int i = 0; i < NUM_THREADS; i++)
		{
			final int start = i * (UPPER_LIMIT / NUM_THREADS) + 1;
			final int end = (i + 1) * (UPPER_LIMIT / NUM_THREADS);

			PrimeThread thread = new PrimeThread(start, end, primes);
			threads.add(thread);
			thread.start();
		}

		// Alternates between threads while making sure they don't overlap
		for (PrimeThread thread : threads)
		{
			try
			{
				thread.join();
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		// Calculates the runtime
		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;

		// Takes the valid prime numbers from the bitset and adds them to an ArrayList
		List<Integer> primeList = new ArrayList<>();
		for (int i = primes.nextSetBit(0); i >= 0; i = primes.nextSetBit(i + 1)) 
		{
			primeList.add(i);
		}

		// Prints the output
		try (FileWriter fileWriter = new FileWriter("primes.txt")) 
		{
			// Prints out the runtime, the number of primes, the sum of all primes
			fileWriter.write(String.format("%d %d %d%n", executionTime, primeList.size(), calculateSum(primeList)));

			// Prints out the 10 greatest primes found
			int size = primeList.size();
			int startIdx = size - Math.min(size, 10);
			List<Integer> topTenPrimes = primeList.subList(startIdx, size);

			for (int prime : topTenPrimes) 
			{
				fileWriter.write(prime + " ");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	// Used for just calculating the final sum of the prines
	private static long calculateSum(List<Integer> nums) 
	{
		long sum = 0;
		for (int num : nums) {
			sum += num;
		}
		return sum;
	}
}
