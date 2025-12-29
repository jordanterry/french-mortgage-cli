class FrenchPropertyInvestment < Formula
  desc "Analyze French rental property investments with comprehensive cash flow projections"
  homepage "https://github.com/jordanterry/french-mortgage-cli"
  url "https://github.com/jordanterry/french-mortgage-cli/archive/refs/tags/v2.0.0.tar.gz"
  sha256 "PLACEHOLDER_SHA256"
  version "2.0.0"
  license "MIT"

  depends_on "python@3.11"

  def install
    libexec.install "python/src/french_mortgage.py"

    (bin/"french-property-investment").write <<~EOS
      #!/bin/bash
      exec "#{Formula["python@3.11"].opt_bin}/python3" "#{libexec}/french_mortgage.py" "$@"
    EOS
  end

  test do
    output = shell_output("#{bin}/french-property-investment --help 2>&1")
    assert_match "French Property Investment", output
  end
end
