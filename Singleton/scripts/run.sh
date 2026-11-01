#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_DIR"

mvn -o -q compile
java -cp target/classes com.artificialintelligence.patterns.singleton.Demo
