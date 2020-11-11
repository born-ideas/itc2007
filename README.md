<!-- PROJECT LOGO -->
<p align="right">
<a href="https://search.maven.org/artifact/dev.born/itc2007">
<img src="https://raw.githubusercontent.com/born-ideas/itc2007/master/assets/project_badge.png" height="100" alt="badge">
</a>
</p>
<p align="center">
<img src="https://raw.githubusercontent.com/born-ideas/itc2007/master/assets/project_logo.png" height="100" alt="logo" />
</p>

<!-- PROJECT SHIELDS -->
<p align="center">
<a href="https://search.maven.org/search?q=g:%22dev.born%22%20AND%20a:%22itc2007%22"><img src="https://img.shields.io/maven-central/v/dev.born/itc2007.svg?label=Maven%20Central" alt="release"></a>
<a href="https://github.com/born-ideas/itc2007/actions?query=workflow%3Abuild"><img src="https://img.shields.io/github/workflow/status/born-ideas/itc2007/build?label=build" alt="build"></a>
<a href="https://github.com/born-ideas/itc2007/issues"><img src="https://img.shields.io/github/issues/born-ideas/itc2007" alt="issues"></a>
<a href="https://github.com/born-ideas/itc2007/network"><img src="https://img.shields.io/github/forks/born-ideas/itc2007" alt="forks"></a>
<a href="https://github.com/born-ideas/itc2007/stargazers"><img src="https://img.shields.io/github/stars/born-ideas/itc2007" alt="stars"></a>
<a href="https://google.github.io/styleguide/javaguide.html"><img src="https://img.shields.io/badge/style-google_java-40c4ff.svg" alt="style"></a>
<a href="https://github.com/born-ideas/itc2007/blob/master/LICENSE"><img src="https://img.shields.io/github/license/born-ideas/itc2007" alt="license"></a>
</p>

---

<!-- TABLE OF CONTENTS -->
## Table of Contents
* [About the Project](#about-the-project)
* [Getting Started](#getting-started)
* [Usage](#usage)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements)



<!-- ABOUT THE PROJECT -->
## About The Project
<p align="center">
<img src="https://raw.githubusercontent.com/born-ideas/itc2007/master/assets/screenshot_1.gif" width="800" alt="Screenshot 1" />
</p>

This Java library provides the classes necessary for working with [ITC2007](http://www.cs.qub.ac.uk/itc2007/index.htm)
Examination Timetabling problem instances.

### Built With
* [Java](https://www.java.com/en/)
* [Apache Maven](https://maven.apache.org)



<!-- GETTING STARTED -->
## Getting Started
### Prerequisites
This library requires JDK11 or later. If this is your first Java project, see the following pages to help you get started:                   
- [Building Java Projects with Maven](https://spring.io/guides/gs/maven/)
- [Building Java Projects with Gradle](https://spring.io/guides/gs/gradle/)

### Installation
Adding the dependency if you're using Apache Maven:
```
<dependency>
  <groupId>dev.born</groupId>
  <artifactId>itc2007</artifactId>
  <version>{version}</version>
</dependency>
```

Adding the dependency if you're using Gradle Groovy DSL:
```
implementation 'dev.born:itc2007:{version}'
```

If you're using other dependency management systems, please visit [search.maven.org/artifact/dev.born/itc2007](https://search.maven.org/artifact/dev.born/itc2007)
and follow the instructions under the version that you want to add as a dependency.



<!-- USAGE EXAMPLES -->
## Usage
Instantiating a new problem instance:
```java
ExamTimetablingProblem problem = ExamTimetablingProblem.fromFile("path/to/problem/file");
```

Creating an initial solution:
```java
ExamTimetablingSolution initialSolution = new ExamTimetablingSolution(problem, List.of());
```

Creating a new solution:
```java
List<Booking> newBookings = new ArrayList<>(currentSolution.bookings);
newBookings.add(new Booking(problem.exams.get(0), problem.periods.get(0), problem.rooms.get(0)));
ExamTimetablingSolution newSolution = new ExamTimetablingSolution(problem, newBookings);
```

Analyzing a solution:
```java
System.out.println(solution.distanceToFeasibility());
System.out.println(solution.softConstraintViolations());
```


<!-- ROADMAP -->
## Roadmap
See the [open issues](https://github.com/born-ideas/itc2007/issues) for a list of other proposed features (and known issues).



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See [LICENSE](LICENSE) for more information.



<!-- CONTACT -->
## Contact

BornIdeas - [born.dev](https://www.born.dev) - [info@born.dev](mailto:support@born.dev)

Project Link: [https://github.com/born-ideas/itc2007](https://github.com/born-ideas/itc2007)



<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements
* [International Timetabling Competition](http://www.cs.qub.ac.uk/itc2007/)
* [Shields IO](https://shields.io)
* [Open Source Licenses](https://choosealicense.com)