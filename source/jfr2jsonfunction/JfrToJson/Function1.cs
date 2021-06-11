using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Azure.Storage;
//using Newtonsoft.Json;
using System.Diagnostics;
using System.Text.Json;

namespace JfrToJson
{
    public static class Function1
    {
        
        [FunctionName("JfrToJsonFunction")]
        public static void Run(
            [BlobTrigger("etl/{name}")] byte[] blobContent,
            string name,
            [Blob("test2/{name}.json", FileAccess.Write)] Stream jsonFileOut,
            [CosmosDB(
                databaseName: "drtest",
                collectionName: "jfrjson",
                ConnectionStringSetting = "CosmosDBConnection")]out dynamic document,
            ILogger log)
        {
            log.LogInformation("Processing a JFR trace");
            string tempFileName = $"{Guid.NewGuid()}.jfr";
            File.WriteAllBytes(tempFileName, blobContent);
            ProcessStartInfo processStartInfo = new ProcessStartInfo();
            processStartInfo.FileName = "jfr";
            processStartInfo.Arguments = "print --json --events CPULoad test.jfr";
            processStartInfo.RedirectStandardOutput = true;
            using var process = new Process()
            {
                StartInfo = processStartInfo
            };
            while(! process.Start());
            using StreamReader reader = process.StandardOutput;
            string cosmosJson = "";
            string line = "";
            using StreamWriter streamWriter = new StreamWriter(jsonFileOut);
            while ((line = reader.ReadLine()) != null || !process.HasExited)
            {
                if (line != null)
                {
                    cosmosJson += line;
                    streamWriter.WriteLine(line);
                }
            }
            process.WaitForExit();
            File.Delete(tempFileName);
            document = new { Document=cosmosJson,File=name, id = Guid.NewGuid() };
            log.LogInformation("process complete");
            
        }
    }
}
