class FrenchPropertyInvestment < Formula
  desc "Analyze French rental property investments with comprehensive cash flow projections"
  homepage "https://github.com/jordanterry/french-mortgage-cli"
  url "https://github.com/jordanterry/french-mortgage-cli/releases/download/v1.0.1/french-property-investment.jar"
  sha256 "57cebbbc6906fd3c5ed9a756921380dac8159621087db81589a0d2d111d41daa"
  version "1.0.1"
  license "MIT"

  depends_on "openjdk@17"

  def install
    libexec.install "french-property-investment.jar"

    # Create wrapper script
    (bin/"french-property-investment").write <<~EOS
      #!/bin/bash
      exec "#{Formula["openjdk@17"].opt_bin}/java" -jar "#{libexec}/french-property-investment.jar" "$@"
    EOS
  end

  test do
    output = shell_output("#{bin}/french-property-investment --help 2>&1")
    assert_match "French Property Investment", output
  end
end
