import time
import random

"""
Mauricio Estrella - CIS5511 - Homework 4
"""
def lomuto_partition(arr, low, high):
    """
    Partitions a sub-array using the Lomuto partition scheme.
    Pivot is the last element.
    Returns the split index and the number of swaps performed.
    """
    swap_count = 0
    pivot = arr[high]
    i = low - 1
    for j in range(low, high):
        if arr[j] <= pivot:
            i += 1
            arr[i], arr[j] = arr[j], arr[i]
            swap_count += 1
            
    arr[i + 1], arr[high] = arr[high], arr[i + 1]
    swap_count += 1
    return i + 1, swap_count

def hoare_partition(arr, low, high):
    """
    Partitions a sub-array using the Hoare partition scheme.
    Pivot is the first element.
    Returns the split index and the number of swaps performed.
    """
    swap_count = 0
    pivot = arr[low]
    i = low - 1
    j = high + 1

    while True:
        i += 1
        while arr[i] < pivot:
            i += 1

        j -= 1
        while arr[j] > pivot:
            j -= 1

        if i >= j:
            return j, swap_count

        arr[i], arr[j] = arr[j], arr[i]
        swap_count += 1

# --- Testing Framework ---
def run_partition_test(partition_func, data_generator, size, trials):
    """
    Runs a timing and swap count test for a given partition function.
    Returns:
        A tuple of (average execution time in ms, average number of swaps).
    """
    total_time = 0
    total_swaps = 0
    for _ in range(trials):
        # generate a fresh copy of the data for each trial
        data = data_generator(size)
        
        start_time = time.perf_counter()
        _, swaps = partition_func(data, 0, size - 1)
        end_time = time.perf_counter()
        
        total_time += (end_time - start_time)
        total_swaps += swaps
        
    avg_time_ms = (total_time / trials) * 1000
    avg_swaps = total_swaps / trials
    
    return avg_time_ms, avg_swaps

# data Generation Functions
def generate_random(size):
    """Generates a list of random integers."""
    return [random.randint(0, size) for _ in range(size)]

def generate_sorted(size):
    """Generates a sorted list."""
    return list(range(size))

def generate_reverse_sorted(size):
    """Generates a reverse-sorted list."""
    return list(range(size, 0, -1))

def generate_many_duplicates(size):
    """Generates a list with many duplicate values."""
    return [random.randint(0, 10) for _ in range(size)]

def main():
    """
    Main function to configure and run the tests.
    """
    # test size
    ARRAY_SIZE = 10000
    NUM_TRIALS = 50

    print(f"Array Size: {ARRAY_SIZE}")
    print(f"Number of Trials: {NUM_TRIALS}")

    test_cases = {
        "Random Data": generate_random,
        "Sorted Data": generate_sorted,
        "Reverse-Sorted Data": generate_reverse_sorted,
        "Many Duplicates": generate_many_duplicates,
    }

    results = {}

    for name, generator in test_cases.items():
        lomuto_time, lomuto_swaps = run_partition_test(lomuto_partition, generator, ARRAY_SIZE, NUM_TRIALS)
        hoare_time, hoare_swaps = run_partition_test(hoare_partition, generator, ARRAY_SIZE, NUM_TRIALS)

        results[name] = {
            "Lomuto Time": lomuto_time, "Lomuto Swaps": lomuto_swaps,
            "Hoare Time": hoare_time, "Hoare Swaps": hoare_swaps
        }

    print("\n" + "="*104)
    print("--- Results (Averages) ---")
    print("="*104)
    
    header1 = (f"{'Data Type':<22} | {'Lomuto Time (ms)':>18} | {'Hoare Time (ms)':>18} | "
               f"{'Lomuto Swaps':>15} | {'Hoare Swaps':>15}")

    print(header1)
    print("-" * 104)

    for name, res in results.items():
        lt = f"{res['Lomuto Time']:.4f}"
        ht = f"{res['Hoare Time']:.4f}"
        ls = f"{res['Lomuto Swaps']:,.0f}"
        hs = f"{res['Hoare Swaps']:,.0f}"
        
        row = (f"{name:<22} | {lt:>18} | {ht:>18} | "
               f"{ls:>15} | {hs:>15}")
        print(row)

    print("-" * 104)

if __name__ == "__main__":
    main()

