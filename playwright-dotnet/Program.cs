using System;
using System.Collections.Generic;

namespace PlaywrightTesting
{
    class Program
    {
        public static async Task Main(string[] args)
        {
            switch (args[0])
            {
                case "single":
                    Console.WriteLine("Running Single Test");
                    await PlaywrightSingleTest.main(args);  // redirects to class main() method
                    break;
                case "parallel":
                    Console.WriteLine("Running Parallel Test");
                    await PlaywrightParallelTest.main(args);
                    break;
                case "local":
                    Console.WriteLine("Running Local Test");
                    await PlaywrightLocalTest.main(args);
                    break;
                default:
                    Console.WriteLine("Running Single Test by default");
                    await PlaywrightSingleTest.main(args);
                    break;
            }
        }
    }
}
